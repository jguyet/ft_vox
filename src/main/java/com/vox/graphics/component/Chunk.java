package com.vox.graphics.component;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

import com.flowpowered.noise.module.source.Perlin;
import com.vox.Factory;
import com.vox.shader.Shader;
import com.vox.utils.Noise;
import com.vox.utils.Sizeof;
import com.vox.utils.Texture;
import com.vox.utils.Util;
import com.vox.utils.Vector;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Chunk extends Component {
	
	public static class Block {
		Vector3f	position;
		float		texture_top = 0;
		float		texture_side = 0;
		float		texture_bottom = 0;
		
		public Block(float x, float y, float z)
		{
			this.position = new Vector3f(x, y, z);
		}
		
		public Block(Vector3f position)
		{
			this.position = position;
		}
	}
	
	public static final int SIZE_STATIC_ARRAY = 8;
	
	public static final int SIZE_WIDTH = 16;
	public static final int SIZE_HEIGHT = 256;
	
	private int		vao;
	private int		vbo_vertexs;
	private int		vbo_uv;
	private int		vbo_normals;
	public static Shader	shader = null;
	
	public static int vertex_location;
	public static int uv_location;
	public static int normal_location;
	public static int color_location;
	public static int projection_location;
	public static int view_location;
	public static int model_location;
	public static int[] texture_diffuse_location = new int[9];
	
	private static FloatBuffer[] vertexs_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	private static FloatBuffer[] uv_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	private static FloatBuffer[] normals_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	public int buffer_index;
	public static boolean[] buffer_wait = new boolean[SIZE_STATIC_ARRAY];
	private int s = 0;
	
	private Map<String, Block> blocks = new HashMap<String, Block>();
	
	public static int seed = new Random().nextInt();
	public static Noise noise;
	public static Perlin perlin;
	public static boolean wait = false;
	
	public boolean vao_builded = false;
	
	static {
		noise = new Noise(new Random().nextInt(), 5555, 0.5f);
		perlin = new Perlin();
		
		for (int i = 0; i < SIZE_STATIC_ARRAY; i++)
		{
			buffer_wait[i] = false;
			ByteBuffer bb1 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) * (6 * 18)));
			vertexs_array[i] = bb1.asFloatBuffer();
			
			ByteBuffer bb4 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) *  (6 * 18)));
			uv_array[i] = bb4.asFloatBuffer();
			
			ByteBuffer bb5 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) *  (6 * 18)));
			normals_array[i] = bb5.asFloatBuffer();
		}
	}
	
	public Chunk()
	{
		
	}
	
	public void destruct()
	{
		blocks.clear();
		if (this.vao_builded == false)
			return ;
		glDeleteVertexArrays(this.vao);
		glDeleteBuffers(this.vbo_vertexs);
		glDeleteBuffers(this.vbo_uv);
		glDeleteBuffers(this.vbo_normals);
	}
	
	public void pre_build_chunk()
	{
		buffer_wait[this.buffer_index] = true;
		build_blocks();
		build_arrays();
	}
	
	private int getNoise(int x, int z)
	{	
		int b1 = ((int)(perlin.getValue((double)((double)(x + 10)/350.0f), 0.40, (double)((double)(z + 10)/350.0f)) * 50) + 100);
		int b2 = ((int)(perlin.getValue((double)((double)(x + 10)/350.0f), 0.40, (double)((double)(z + 10)/350.0f)) * 90) + 100);
		return (b1);
	}
	
	public void build_blocks()
	{
		int sx, sy, sz;
		sx = (int)this.gameObject.transform.position.x;
		sy = (int)this.gameObject.transform.position.y;
		sz = (int)this.gameObject.transform.position.z;
		for (int x = sx - (Chunk.SIZE_WIDTH / 2); x < sx + (Chunk.SIZE_WIDTH / 2); x++)
		{
			for (int z = sz - (Chunk.SIZE_WIDTH / 2); z < sz + (Chunk.SIZE_WIDTH / 2); z++)
			{
				int height = getNoise(x, z);
				
				if (height <= 62)
				{
					String key = "" + (x - sx) + "," + (62 - sy) + "," + (z - sz);
					blocks.put(key, new Block((x - sx), (62 - sy), (z - sz)));
					height--;
				}
				
				for (int h = (height + 1); h > 0; h--)
				{
					boolean has_up = height > h;
					boolean has_left = getNoise(x - 1, z) > h;
					boolean has_right = getNoise(x + 1, z) > h;
					boolean has_front = getNoise(x, z + 1) > h;
					boolean has_back = getNoise(x, z - 1) > h;
					
					if (has_up == false || has_left == false || has_right == false || has_front == false || has_back == false)
					{
						String key = "" + (x - sx) + "," + (h - sy) + "," + (z - sz);
						Block b = new Block(new Vector3f((x - sx), (h - sy), (z - sz)));
						
						b.texture_top = 0;
						b.texture_side = 2.1f;//terre
						b.texture_bottom = 2.1f;//terre
						
						if (h >= 150)//neige
						{
							b.texture_top = 3.1f;
							b.texture_side = 4.1f;
						}
						if (has_up || h < 62)
						{
							b.texture_top = 2.1f;//terre
							b.texture_side = 2.1f;//terre
						}
						
						if (h < 70 && h > 64 && has_up == false && Util.getRandomValue(1, 100) > 98)
						{
							String key2 = "" + (x - sx) + "," + ((h + 1) - sy) + "," + (z - sz);
							Block b2 = new Block(new Vector3f((x - sx), ((h + 1) - sy), (z - sz)));
							
							b2.texture_top = 6.1f;//cactus
							b2.texture_side = 5.1f;
							b2.texture_bottom = 6.1f;
							blocks.put(key2, b2);
						}
						
						if (h < 72 && h > 60)
						{
							b.texture_top = 7.1f;//terre
							b.texture_side = 7.1f;//terre
						}
						
						blocks.put(key, b);
					}
					else
					{
						break ;
					}
				}
			}
		}
	}
	
	public void build_arrays()
	{	
		Chunk.vertexs_array[buffer_index].clear();
		Chunk.uv_array[buffer_index].clear();
		Chunk.normals_array[buffer_index].clear();
		
		int sx, sy, sz;
		sx = (int)this.gameObject.transform.position.x;
		sy = (int)this.gameObject.transform.position.y;
		sz = (int)this.gameObject.transform.position.z;
		
		for (Block obj : blocks.values())
		{
			
			int bx = (int)obj.position.x;
			int by = (int)obj.position.y;
			int bz = (int)obj.position.z;
			float x = obj.position.x;
			float y = obj.position.y;
			float z = obj.position.z;
			
			boolean has_up = getNoise(sx + bx, sz + bz) > (sy + by);
			boolean has_left = getNoise(sx + (bx - 1), sz + (bz)) > (sy + by);
			boolean has_right = getNoise(sx + (bx + 1), sz + (bz)) > (sy + by);
			boolean has_front = getNoise(sx + (bx), sz + (bz + 1)) > (sy + by);
			boolean has_back = getNoise(sx + (bx), sz + (bz - 1)) > (sy + by);
			
			if ((sy + by) == 62)
				continue ;
			
			if (has_front == false && blocks.containsKey("" + bx + "," + by + "," + (bz + 1)) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_front(x,y,z));
				uv_array[buffer_index].put(Factory.texture_front(obj.texture_side));
				normals_array[buffer_index].put(Factory.normal_front());
			}
			if (has_back == false && blocks.containsKey("" + bx + "," + by + "," + (bz - 1)) == false)//ok
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_back(x,y,z));
				uv_array[buffer_index].put(Factory.texture_back(obj.texture_side));
				normals_array[buffer_index].put(Factory.normal_back());
			}
			if (has_left == false && blocks.containsKey("" + (bx - 1) + "," + by + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_left(x,y,z));
				uv_array[buffer_index].put(Factory.texture_left(obj.texture_side));
				normals_array[buffer_index].put(Factory.normal_left());
			}
			if (has_right == false && blocks.containsKey("" + (bx + 1) + "," + by + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_right(x,y,z));
				uv_array[buffer_index].put(Factory.texture_right(obj.texture_side));
				normals_array[buffer_index].put(Factory.normal_right());
			}
			if (has_up == false && blocks.containsKey("" + bx + "," + (by + 1) + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array[buffer_index].put(Factory.block_top(x,y,z));
				uv_array[buffer_index].put(Factory.texture_top(obj.texture_top));
				normals_array[buffer_index].put(Factory.normal_top());
			}
//			if (blocks.containsKey("" + bx + "," + (by - 1) + "," + bz) == false)
//			{
//				this.s += 2;
//				vertexs_array[buffer_index].put(Factory.block_top(x,y,z));
//				uv_array[buffer_index].put(Factory.texture_side());
//			}
		}
		
		for (Block obj : blocks.values())//water
		{
			int by = (int)obj.position.y;
			float x = obj.position.x;
			float y = obj.position.y;
			float z = obj.position.z;
			
			
			if ((sy + by) != 62)
			{
				continue ;
			}
			
			this.s += 2;
			vertexs_array[buffer_index].put(Factory.block_top(x,y,z));
			uv_array[buffer_index].put(Factory.texture_top(-10));
			normals_array[buffer_index].put(Factory.normal_top());
		}
		
		Chunk.vertexs_array[buffer_index].limit(Chunk.vertexs_array[buffer_index].position());
		Chunk.uv_array[buffer_index].limit(Chunk.uv_array[buffer_index].position());
		Chunk.normals_array[buffer_index].limit(Chunk.normals_array[buffer_index].position());
		vertexs_array[buffer_index].flip();
		uv_array[buffer_index].flip();
		normals_array[buffer_index].flip();
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
		texture_diffuse_location[3] = glGetUniformLocation(shader.id, "u_texture_diffuse[3]");
		texture_diffuse_location[4] = glGetUniformLocation(shader.id, "u_texture_diffuse[4]");
		texture_diffuse_location[5] = glGetUniformLocation(shader.id, "u_texture_diffuse[5]");
		texture_diffuse_location[6] = glGetUniformLocation(shader.id, "u_texture_diffuse[6]");
		texture_diffuse_location[7] = glGetUniformLocation(shader.id, "u_texture_diffuse[7]");
		texture_diffuse_location[8] = glGetUniformLocation(shader.id, "u_texture_diffuse[8]");
		//glBindFragDataLocation(this.shader.id, 0, "o_color");
	}
	
	public void build_vao()
	{
		this.vao = glGenVertexArrays();
		glBindVertexArray(this.vao);
		//VERTEXS
		vbo_vertexs = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_vertexs);
		glBufferData(GL_ARRAY_BUFFER, vertexs_array[buffer_index], GL_STATIC_DRAW);
		glEnableVertexAttribArray(Chunk.vertex_location);
		glVertexAttribPointer(Chunk.vertex_location, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		vbo_uv = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_uv);
		glBufferData(GL_ARRAY_BUFFER, uv_array[buffer_index], GL_STATIC_DRAW);
		glEnableVertexAttribArray(Chunk.uv_location);
		glVertexAttribPointer(Chunk.uv_location, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		vbo_normals = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_normals);
		glBufferData(GL_ARRAY_BUFFER, normals_array[buffer_index], GL_STATIC_DRAW);
		glEnableVertexAttribArray(Chunk.normal_location);
		glVertexAttribPointer(Chunk.normal_location, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
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
		glUniform1i(Chunk.texture_diffuse_location[3], 3);
		glActiveTexture(GL_TEXTURE0 + 3);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("snow_top").id);
		glUniform1i(Chunk.texture_diffuse_location[4], 4);
		glActiveTexture(GL_TEXTURE0 + 4);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("snow_side").id);
		glUniform1i(Chunk.texture_diffuse_location[5], 5);
		glActiveTexture(GL_TEXTURE0 + 5);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("cactus_side").id);
		glUniform1i(Chunk.texture_diffuse_location[6], 6);
		glActiveTexture(GL_TEXTURE0 + 6);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("cactus_top").id);
		glUniform1i(Chunk.texture_diffuse_location[7], 7);
		glActiveTexture(GL_TEXTURE0 + 7);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("sand").id);
		glUseProgram(0);
		buffer_wait[this.buffer_index] = false;
		this.vao_builded = true;
		this.buffer_index = -1;
	}
	
	public void draw(FloatBuffer projection, FloatBuffer view, FloatBuffer model, Vector3f light)
	{
		glUniformMatrix4fv(Chunk.model_location, false, model);
		
		glBindVertexArray(this.vao);
		glDrawArrays(GL_TRIANGLES, 0, this.s * 3);
		glBindVertexArray(0);
	}
}
