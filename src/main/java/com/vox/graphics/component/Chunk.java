package com.vox.graphics.component;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

import com.flowpowered.noise.module.source.Perlin;
import com.vox.Factory;
import com.vox.shader.Shader;
import com.vox.utils.Noise;
import com.vox.utils.Sizeof;
import com.vox.utils.Texture;
import com.vox.utils.Util;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Chunk extends Component {

	//public static Map<String, Chunk>	chunks = new HashMap<String, Chunk>();
	
	public static final int SIZE_STATIC_ARRAY = 40;
	
	public static final int SIZE_WIDTH = 16;
	public static final int SIZE_HEIGHT = 16;
	
	private int		vao;
	private int		vbo_vertexs;
	private int		vbo_uv;
	private int		vbo_normals;
	private int		vbo_colors;
	public static Shader	shader = null;
	
	public static int vertex_location;
	public static int uv_location;
	public static int normal_location;
	public static int color_location;
	public static int projection_location;
	public static int view_location;
	public static int model_location;
	public static int[] texture_diffuse_location = new int[5];
	
	private static FloatBuffer[] vertexs_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	private static FloatBuffer[] uv_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	public int buffer_index;
	public static boolean[] buffer_wait = new boolean[SIZE_STATIC_ARRAY];
	private int s = 0;
	
	private Map<String, Vector3f> blocks = new HashMap<String, Vector3f>();
	
	public static int seed = new Random().nextInt();
	public static Noise noise;
	public static Perlin perlin;
	public static boolean wait = false;
	
	public boolean vao_builded = false;
	
	static {
		noise = new Noise();
		noise.setSeed(new Random().nextInt());
		noise.build();
		perlin = new Perlin();
		
		for (int i = 0; i < SIZE_STATIC_ARRAY; i++)
		{
			buffer_wait[i] = false;
			ByteBuffer bb1 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) * (6 * 18)));
			vertexs_array[i] = bb1.asFloatBuffer();
			
			ByteBuffer bb4 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) *  (6 * 18)));
			uv_array[i] = bb4.asFloatBuffer();
		}
	}
	
	public Chunk()
	{
		
	}
	
	public void pre_build_chunk()
	{
//		String key = "" + (int)Math.floor(this.gameObject.transform.position.x / Chunk.SIZE_WIDTH) + ","
//						+ (int)Math.floor(this.gameObject.transform.position.y / Chunk.SIZE_HEIGHT) + ","
//						+ (int)Math.floor(this.gameObject.transform.position.z / Chunk.SIZE_WIDTH);
		
		//chunks.put(key, this);
		build_blocks();
		build_arrays();
	}
	
	public void build_blocks()
	{
		//int h = Util.getRandomValue(2, 4);
		int sx, sy, sz;
		sx = (int)this.gameObject.transform.position.x;
		sy = (int)this.gameObject.transform.position.y;
		sz = (int)this.gameObject.transform.position.z;
		for (int x = sx - (Chunk.SIZE_WIDTH / 2); x < sx + (Chunk.SIZE_WIDTH / 2); x++)
		{
			for (int z = sz - (Chunk.SIZE_WIDTH / 2); z < sz + (Chunk.SIZE_WIDTH / 2); z++)
			{
				int height = (int)(perlin.getValue((double)((double)(x + 10)/300.0f), 0.40, (double)((double)(z + 10)/300.0f)) * 50) + 10;
				
				for (int y = sy - (Chunk.SIZE_HEIGHT / 2); y < sy + (Chunk.SIZE_HEIGHT / 2); y++)
				{
					if (height < y)
						continue ;
					String key = "" + (x - sx) + "," + (y - sy) + "," + (z - sz);
					blocks.put(key, new Vector3f((x - sx), (y - sy), (z - sz)));
				}
			}
		}
	}
	
	public void build_arrays()
	{	
		Chunk.vertexs_array[buffer_index].clear();
		Chunk.uv_array[buffer_index].clear();
		for (Vector3f obj : blocks.values())
		{
			
			int bx = (int)obj.x;
			int by = (int)obj.y;
			int bz = (int)obj.z;
			float x = obj.x;
			float y = obj.y;
			float z = obj.z;
			
			if (this.gameObject.transform.position.y + y < -1)
				continue ;
			
			if (blocks.containsKey("" + bx + "," + by + "," + (bz + 1)) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_front(x,y,z));
				uv_array[buffer_index].put(Factory.texture_side());
			}
			if (blocks.containsKey("" + bx + "," + by + "," + (bz - 1)) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_back(x,y,z));
				uv_array[buffer_index].put(Factory.texture_side());
			}
			if (blocks.containsKey("" + (bx - 1) + "," + by + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_left(x,y,z));
				uv_array[buffer_index].put(Factory.texture_side());
			}
			if (blocks.containsKey("" + (bx + 1) + "," + by + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_right(x,y,z));
				uv_array[buffer_index].put(Factory.texture_side());
			}
			if (blocks.containsKey("" + bx + "," + (by + 1) + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_top(x,y,z));
				uv_array[buffer_index].put(Factory.texture_front());
			}
			if (blocks.containsKey("" + bx + "," + (by - 1) + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_top(x,y,z));
				uv_array[buffer_index].put(Factory.texture_side());
			}
		}
		
		Chunk.vertexs_array[buffer_index].limit(Chunk.vertexs_array[buffer_index].position());
		Chunk.uv_array[buffer_index].limit(Chunk.uv_array[buffer_index].position());
		vertexs_array[buffer_index].flip();
		uv_array[buffer_index].flip();
	}
	
	public static void build_shader()
	{
		if (shader != null)
			return ;
		shader = new Shader("assets/shaders/global.vert", "assets/shaders/global.frag");
		vertex_location = glGetAttribLocation(shader.id, "a_v");
		normal_location = glGetAttribLocation(shader.id, "a_n");
		uv_location = glGetAttribLocation(shader.id, "a_uv");
		color_location = glGetAttribLocation(shader.id, "a_color");
		projection_location = glGetUniformLocation(shader.id, "P");
		view_location = glGetUniformLocation(shader.id, "V");
		model_location = glGetUniformLocation(shader.id, "M");
		texture_diffuse_location[0] = glGetUniformLocation(shader.id, "u_texture_diffuse[0]");
		texture_diffuse_location[1] = glGetUniformLocation(shader.id, "u_texture_diffuse[1]");
		texture_diffuse_location[2] = glGetUniformLocation(shader.id, "u_texture_diffuse[2]");
		//glBindFragDataLocation(this.shader.id, 0, "o_color");
	}
	
	public void build_vao()
	{	
		//build_shader();
		this.vao = glGenVertexArrays();
		glBindVertexArray(this.vao);
		//Chunk.normals_array.limit(Chunk.normals_array.position());
		//Chunk.colors_array.limit(Chunk.colors_array.position());
		//VERTEXS
		vbo_vertexs = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_vertexs);
		glBufferData(GL_ARRAY_BUFFER, vertexs_array[buffer_index], GL_STATIC_DRAW);
		glEnableVertexAttribArray(Chunk.vertex_location);
		glVertexAttribPointer(Chunk.vertex_location, 3, GL_FLOAT, false, 0, 0);
		vbo_uv = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_uv);
		glBufferData(GL_ARRAY_BUFFER, uv_array[buffer_index], GL_STATIC_DRAW);
		glEnableVertexAttribArray(Chunk.uv_location);
		glVertexAttribPointer(Chunk.uv_location, 3, GL_FLOAT, false, 0, 0);
		glBindVertexArray(0);
		
		glUseProgram(Chunk.shader.id);
		glUniform1i(Chunk.texture_diffuse_location[0], 0);
		glActiveTexture(GL_TEXTURE0 + 0);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_top").id);
		glUniform1i(Chunk.texture_diffuse_location[1], 1);
		glActiveTexture(GL_TEXTURE0 + 1);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_side").id);
		glUniform1i(Chunk.texture_diffuse_location[2], 2);
		glActiveTexture(GL_TEXTURE0 + 2);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_bottom").id);
		glUseProgram(0);
		this.vao_builded = true;
		buffer_wait[this.buffer_index] = false;
	}
	
	public void draw(FloatBuffer projection, FloatBuffer view, FloatBuffer model, Vector3f light)
	{	
		//glUseProgram(this.shader.id);
		
		//glUniformMatrix4fv(this.projection_location, false, projection);
		//glUniformMatrix4fv(this.view_location, false, view);
		glUniformMatrix4fv(Chunk.model_location, false, model);
		//glUniform3f(glGetUniformLocation(this.shader.id, "light_worldspace"), light.x, light.y + 10, light.z);
		
//		int[] exponents = new int[3];
//		exponents[0] = Texture.list.get("grass_top").id;
//		exponents[1] = Texture.list.get("grass_side").id;
//		exponents[2] = Texture.list.get("grass_bottom").id;
		
		//glUniform1iv(this.texture_diffuse_location[0], exponents);
		//glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_top").id);
		//glActiveTexture(GL_TEXTURE0 + 1);
		//glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_side").id);
		//glUniform1i(this.texture_diffuse_location[1], 0);
		//glActiveTexture(GL_TEXTURE0 + 2);
		//glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_bottom").id);
		//glUniform1i(this.texture_diffuse_location[2], 0);
		
		glBindVertexArray(this.vao);
		glDrawArrays(GL_TRIANGLES, 0, this.s * 3);
		glBindVertexArray(0);
		
		//glUseProgram(0);
	}
}
