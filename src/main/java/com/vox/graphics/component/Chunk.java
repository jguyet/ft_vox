package com.vox.graphics.component;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import com.vox.Factory;
import com.vox.shader.Shader;
import com.vox.utils.Sizeof;
import com.vox.utils.Util;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class Chunk extends Component {

	private static Map<String, Chunk>	chunks = new HashMap<String, Chunk>();
	
	public static final int SIZE_WIDTH = 16;
	public static final int SIZE_HEIGHT = 5;
	
	private int		vao;
	private int		vbo_vertexs;
	private int		vbo_normals;
	private Shader	shader;
	
	private int vertex_location;
	private int normal_location;
	private int color_location;
	private int projection_location;
	private int view_location;
	private int model_location;
	
	private static FloatBuffer vertexs_array;
	private static FloatBuffer normals_array;
	private static FloatBuffer colors_array;
	private int s = 0;
	
	private Map<String, Vector3f> blocks = new HashMap<String, Vector3f>();
	
	static {
		ByteBuffer bb1 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) * (6 * 18)));
		vertexs_array = bb1.asFloatBuffer();
		
		ByteBuffer bb2 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) *  (6 * 18)));
		normals_array = bb2.asFloatBuffer();
		
		ByteBuffer bb3 = BufferUtils.createByteBuffer(Sizeof.Float((Chunk.SIZE_WIDTH * Chunk.SIZE_WIDTH * Chunk.SIZE_HEIGHT) *  (6 * 18)));
		colors_array = bb3.asFloatBuffer();
	}
	
	public Chunk()
	{
		
	}
	
	public void build_chunk()
	{
		String key = "" + (int)Math.floor(this.gameObject.transform.position.x / Chunk.SIZE_WIDTH) + ","
						+ (int)Math.floor(this.gameObject.transform.position.y / Chunk.SIZE_HEIGHT) + ","
						+ (int)Math.floor(this.gameObject.transform.position.z / Chunk.SIZE_WIDTH);
		
		chunks.put(key, this);
		build_blocks();
		build_arrays();
		build_shader();
		build_vao();
	}
	
	public void build_blocks()
	{
		for (int x = -(Chunk.SIZE_WIDTH / 2); x < (Chunk.SIZE_WIDTH / 2); x++)
		{
			for (int z = -(Chunk.SIZE_WIDTH / 2); z < (Chunk.SIZE_WIDTH / 2); z++)
			{
				for (int y = -(Chunk.SIZE_HEIGHT / 2); y < (Chunk.SIZE_HEIGHT / 2); y++)
				{
					if (Util.getRandomValue(1, 10) > 8)
						continue ;
					String key = "" + x + "," + y + "," + z;
					blocks.put(key, new Vector3f(x, y, z));
				}
			}
		}
	}
	
	public void build_arrays()
	{	
		
		float[] green_color = {
				0.54f, 0.29f, 0.03f,
				0.54f, 0.29f, 0.03f,
				0.54f, 0.29f, 0.03f,
				0.54f, 0.29f, 0.03f,
				0.54f, 0.29f, 0.03f,
				0.54f, 0.29f, 0.03f,
		};
		
		float[] top_color = {
				0.0f, 0.87f, 0.23f,
				0.0f, 0.87f, 0.23f,
				0.0f, 0.87f, 0.23f,
				0.0f, 0.87f, 0.23f,
				0.0f, 0.87f, 0.23f,
				0.0f, 0.87f, 0.23f,
		};
		
		for (Vector3f obj : blocks.values())
		{
			
			int bx = (int)obj.x;
			int by = (int)obj.y;
			int bz = (int)obj.z;
			float x = this.gameObject.transform.position.x + obj.x;
			float y = this.gameObject.transform.position.y + obj.y;
			float z = this.gameObject.transform.position.z + obj.z;
			
			if (blocks.containsKey("" + bx + "," + by + "," + (bz + 1)) == false)
			{
				this.s += 2;
				vertexs_array.put(Factory.block_front(x,y,z));
				normals_array.put(Factory.normal_top());
				colors_array.put(green_color);
			}
			if (blocks.containsKey("" + bx + "," + by + "," + (bz - 1)) == false)
			{
				this.s += 2;
				vertexs_array.put(Factory.block_back(x,y,z));
				normals_array.put(Factory.normal_top());
				colors_array.put(green_color);
			}
			if (blocks.containsKey("" + (bx - 1) + "," + by + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array.put(Factory.block_left(x,y,z));
				normals_array.put(Factory.normal_top());
				colors_array.put(green_color);
			}
			if (blocks.containsKey("" + (bx + 1) + "," + by + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array.put(Factory.block_right(x,y,z));
				normals_array.put(Factory.normal_top());
				colors_array.put(green_color);
			}
			if (blocks.containsKey("" + bx + "," + (by + 1) + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array.put(Factory.block_top(x,y,z));
				normals_array.put(Factory.normal_top());
				colors_array.put(top_color);
			}
			if (blocks.containsKey("" + bx + "," + (by - 1) + "," + bz) == false)
			{
				this.s += 2;
				vertexs_array.put(Factory.block_bottom(x,y,z));
				normals_array.put(Factory.normal_bottom());
				colors_array.put(green_color);
			}
		}
	}
	
	public void build_shader()
	{
		this.shader = new Shader("assets/shaders/global.vert", "assets/shaders/global.frag");
		this.vertex_location = glGetAttribLocation(this.shader.id, "a_v");
		this.normal_location = glGetAttribLocation(this.shader.id, "a_n");
		this.color_location = glGetAttribLocation(this.shader.id, "a_color");
		this.projection_location = glGetUniformLocation(this.shader.id, "P");
		this.view_location = glGetUniformLocation(this.shader.id, "V");
		this.model_location = glGetUniformLocation(this.shader.id, "M");
		//glBindFragDataLocation(this.shader.id, 0, "o_color");
	}
	
	public void build_vao()
	{	
		this.vao = glGenVertexArrays();
		glBindVertexArray(this.vao);
		this.vertexs_array.limit(this.vertexs_array.position());
		this.normals_array.limit(this.normals_array.position());
		this.colors_array.limit(this.colors_array.position());
		
		vertexs_array.flip();
		//VERTEXS
		vbo_vertexs = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_vertexs);
		glBufferData(GL_ARRAY_BUFFER, vertexs_array, GL_STATIC_DRAW);
		glVertexAttribPointer(this.vertex_location, 3, GL_FLOAT, false, 0, 0);
		//NORMALS
		normals_array.flip();
		vbo_normals = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_normals);
		glBufferData(GL_ARRAY_BUFFER, normals_array, GL_STATIC_DRAW);
		glVertexAttribPointer(this.normal_location, 3, GL_FLOAT, false, 0, 0);
		//COLORS
		colors_array.flip();
		vbo_normals = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo_normals);
		glBufferData(GL_ARRAY_BUFFER, colors_array, GL_STATIC_DRAW);
		glVertexAttribPointer(this.color_location, 3, GL_FLOAT, false, 0, 0);
		
		glBindVertexArray(0);
		
		this.vertexs_array.clear();
		this.normals_array.clear();
		this.colors_array.clear();
	}
	
	public void draw(FloatBuffer projection, FloatBuffer view, FloatBuffer model, Vector3f light)
	{	
		glUseProgram(this.shader.id);
		
		glUniformMatrix4fv(this.projection_location, false, projection);
		glUniformMatrix4fv(this.view_location, false, view);
		glUniformMatrix4fv(this.model_location, false, model);
		glUniform3f(glGetUniformLocation(this.shader.id, "light_worldspace"), light.x, light.y, light.z);
		
		glBindVertexArray(this.vao);
		glEnableVertexAttribArray(this.vertex_location);
		glEnableVertexAttribArray(this.normal_location);
		glEnableVertexAttribArray(this.color_location);
		glDrawArrays(GL_TRIANGLES, 0, this.s * 3);
		glBindVertexArray(0);
		
		glUseProgram(0);
	}
}
