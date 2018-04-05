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
import com.vox.graphics.api.VertexArrayObject;
import com.vox.shader.ChunkShader;
import com.vox.utils.Block;
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
	
	public static class NoiseData
	{
	    NoiseData(float SpartialScale, float HeightScale, float Octaves)
	    {
	    	this.SpartialScale = SpartialScale;
	    	this.HeightScale = HeightScale;
	    	this.Octaves = Octaves;
	    }

	    public float SpartialScale;
	    public float HeightScale;
	    public float Octaves;
	}
	
	public static final int			SIZE_STATIC_ARRAY	= 4;
	public static final int			SIZE_WIDTH			= 16;
	public static final int			SIZE_HEIGHT			= 256;
	
	private static FloatBuffer[]	vertexs_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	private static FloatBuffer[]	uv_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	private static FloatBuffer[]	normals_array = new FloatBuffer[SIZE_STATIC_ARRAY];
	public static boolean[]			buffer_wait = new boolean[SIZE_STATIC_ARRAY];
	public static int				seed = new Random().nextInt();
	public static Perlin			perlin;
	public static NoiseData[]		noises = new NoiseData[3];
	
	public int						buffer_index;
	private Map<String, Block>		blocks = new HashMap<String, Block>();
	public boolean					vao_builded = false;
	public VertexArrayObject		vertexArrayObject;
	public ChunkShader				shader = null;
	
	static {
		perlin = new Perlin();
		perlin.setSeed(Chunk.seed);
		//perlin.setPersistence(5000.0);
		//perlin.setOctaveCount(1);
		
		noises[0] = new NoiseData(100.0f, 80.0f, 1);
		noises[1] = new NoiseData(12.0f, 60.0f, 1);
		noises[2] = new NoiseData(5.0f, 10.0f, 1);
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
	
	public Chunk(ChunkShader s)
	{
		this.vertexArrayObject = null;
		this.shader = s;
	}
	
	public void destruct()
	{
		blocks.clear();
		if (this.vao_builded == false)
			return ;
		if (this.vertexArrayObject != null)
			this.vertexArrayObject.delete();
	}
	
	public void pre_build_chunk(int id)
	{
		this.buffer_index = id;
		this.vertexArrayObject = new VertexArrayObject(vertexs_array[id], uv_array[id], normals_array[id]);
		buffer_wait[id] = true;
		build_blocks();
		build_arrays();
	}
	
	private int getNoise(int x, int z)
	{	
		double perlinx = ((x + 10.0)/350.0);
		double perlinz = ((z + 10.0)/350.0);
		float height = 0;
		
//		for (int i = 0; i < 3; i++)
//		{
//			perlin.setLacunarity(noises[i].SpartialScale);;
//			perlin.setOctaveCount((int) noises[i].Octaves);
//			height = height + ((float)(perlin.getValue(perlinx, 0.40, perlinz) * noises[i].HeightScale));
//		}
		height = ((float)(perlin.getValue(perlinx, 0.40, perlinz) * 90) + 40);
		return ((int)height);
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
					blocks.put(key, new Block(this, (x - sx), (62 - sy), (z - sz)));
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
						Block b = new Block(this, new Vector3f((x - sx), (h - sy), (z - sz)));
						
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
							Block b2 = new Block(this, new Vector3f((x - sx), ((h + 1) - sy), (z - sz)));
							
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
		this.vertexArrayObject.clear();
		
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
				this.vertexArrayObject.add(Factory.block_front(x,y,z),
						Factory.texture_front(obj.texture_side),
						Factory.normal_front());
			}
			if (has_back == false && blocks.containsKey("" + bx + "," + by + "," + (bz - 1)) == false)
			{
				this.vertexArrayObject.add(Factory.block_back(x,y,z),
						Factory.texture_back(obj.texture_side),
						Factory.normal_back());
			}
			if (has_left == false && blocks.containsKey("" + (bx - 1) + "," + by + "," + bz) == false)
			{
				this.vertexArrayObject.add(Factory.block_left(x,y,z),
						Factory.texture_left(obj.texture_side),
						Factory.normal_left());
			}
			if (has_right == false && blocks.containsKey("" + (bx + 1) + "," + by + "," + bz) == false)
			{
				this.vertexArrayObject.add(Factory.block_right(x,y,z),
						Factory.texture_right(obj.texture_side),
						Factory.normal_right());
			}
			if (has_up == false && blocks.containsKey("" + bx + "," + (by + 1) + "," + bz) == false)
			{
				this.vertexArrayObject.add(Factory.block_top(x,y,z),
						Factory.texture_top(obj.texture_top),
						Factory.normal_top());
			}
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
			this.vertexArrayObject.add(Factory.block_top(x,y,z),
					Factory.texture_top(-10),
					Factory.normal_top());
		}
		
		this.vertexArrayObject.flip();
	}
	
	public void build_vao()
	{
		this.vertexArrayObject.bind(shader.vertex_location, shader.uv_location, shader.normal_location);
		
		glUseProgram(shader.id);
		glUniform1i(shader.texture_diffuse_location[0], 0);
		glActiveTexture(GL_TEXTURE0 + 0);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_top").id);
		glUniform1i(shader.texture_diffuse_location[1], 1);
		glActiveTexture(GL_TEXTURE0 + 1);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_side").id);
		glUniform1i(shader.texture_diffuse_location[2], 2);
		glActiveTexture(GL_TEXTURE0 + 2);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("grass_bottom").id);
		glUniform1i(shader.texture_diffuse_location[3], 3);
		glActiveTexture(GL_TEXTURE0 + 3);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("snow_top").id);
		glUniform1i(shader.texture_diffuse_location[4], 4);
		glActiveTexture(GL_TEXTURE0 + 4);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("snow_side").id);
		glUniform1i(shader.texture_diffuse_location[5], 5);
		glActiveTexture(GL_TEXTURE0 + 5);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("cactus_side").id);
		glUniform1i(shader.texture_diffuse_location[6], 6);
		glActiveTexture(GL_TEXTURE0 + 6);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("cactus_top").id);
		glUniform1i(shader.texture_diffuse_location[7], 7);
		glActiveTexture(GL_TEXTURE0 + 7);
		glBindTexture(GL_TEXTURE_2D, Texture.list.get("sand").id);
		glUseProgram(0);
		buffer_wait[this.buffer_index] = false;
		this.vao_builded = true;
		this.buffer_index = -1;
	}
	
	public void draw(FloatBuffer projection, FloatBuffer view, FloatBuffer model, Vector3f light)
	{
		glUseProgram(this.shader.id);
		glUniformMatrix4fv(this.shader.projection_location, false, projection);
		glUniformMatrix4fv(this.shader.view_location, false, view);
		glUniform3f(glGetUniformLocation(this.shader.id, "light_worldspace"), light.x + 100, 50, light.z + 100);
		
		glUniformMatrix4fv(shader.model_location, false, model);
		this.vertexArrayObject.draw(GL_TRIANGLES);
		glUseProgram(0);
	}
}
