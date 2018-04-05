package com.vox;

import java.util.HashMap;
import java.util.Map;

import com.vox.shader.ChunkShader;
import com.vox.shader.Shader;
import com.vox.utils.Texture;

public class Factory {
	
	public static Map<String, Shader> shaders = new HashMap<String, Shader>();
	
	public static void load_textures()
	{
		Texture.loadTexture2D("grass_top", "assets/textures/grass_top.png");
		Texture.loadTexture2D("grass_side", "assets/textures/soul_sand.png");
		Texture.loadTexture2D("grass_bottom", "assets/textures/soul_sand.png");
		Texture.loadTexture2D("snow_side", "assets/textures/soul_sand.png");
		Texture.loadTexture2D("snow_top", "assets/textures/snow.png");
		Texture.loadTexture2D("cactus_side", "assets/textures/cactus_side.png");
		Texture.loadTexture2D("cactus_top", "assets/textures/cactus_top.png");
		Texture.loadTexture2D("sand", "assets/textures/sand.png");
		Texture.loadTexture2D("grass", "assets/textures/grass_one.png");
	}
	
	public static void load_shaders()
	{
		shaders.put("ChunkShader", new ChunkShader("assets/shaders/global.vert", "assets/shaders/global.frag"));
	}

	public static float[]	block_front(float x, float y, float z)
	{
		float[] array = {
				// Front face
				x + 0.5f, y + -0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f,
				x + -0.5f, y + -0.5f, z + 0.5f,
				
				x + 0.5f, y + 0.5f, z + 0.5f,
				x + -0.5f, y + 0.5f, z + 0.5f,
				x + -0.5f, y + -0.5f, z + 0.5f,
		};
		return (array);
	}
	
	public static float[]	block_back(float x, float y, float z)
	{
		float[] array = {
				// Back face
				x + -0.5f, y + -0.5f, z + -0.5f,
				x + -0.5f, y + 0.5f, z + -0.5f,
				x + 0.5f, y + 0.5f, z + -0.5f,
				
				x + -0.5f, y + -0.5f, z + -0.5f,
				x + 0.5f, y + 0.5f, z + -0.5f,
				x + 0.5f, y + -0.5f, z + -0.5f
		};
		return (array);
	}
	
	public static float[]	block_left(float x, float y, float z)
	{
		float[] array = {
				// Left face
				x - 0.5f, y + -0.5f, z + 0.5f,
				x - 0.5f, y + 0.5f, z + 0.5f,
				x - 0.5f, y + 0.5f, z + -0.5f,
				
				x - 0.5f, y + 0.5f, z + -0.5f,
				x - 0.5f, y + -0.5f, z + -0.5f,
				x - 0.5f, y + -0.5f, z + 0.5f,
				
		        
		};
		return (array);
	}
	
	public static float[]	block_right(float x, float y, float z)
	{
		float[] array = {
				// Right face
				x + 0.5f, y + 0.5f, z + -0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f,
				x + 0.5f, y + -0.5f, z + 0.5f,
				
				x + 0.5f, y + -0.5f, z + 0.5f,
				x + 0.5f, y + -0.5f, z + -0.5f,
				x + 0.5f, y + 0.5f, z + -0.5f
		};
		return (array);
	}
	
	public static float[]	block_top(float x, float y, float z)
	{
		float[] array = {
				// Top face
				x + -0.5f, y + 0.5f, z + 0.5f,//01 hr
				x + 0.5f, y + 0.5f, z + 0.5f,//11  br
				x + 0.5f, y + 0.5f, z + -0.5f,//10 bl
				
				x + -0.5f, y + 0.5f, z + 0.5f,//01 hr
				x + 0.5f, y + 0.5f, z + -0.5f,//10 bl
				x + -0.5f, y + 0.5f, z + -0.5f//00 hl
		};
		return (array);
	}
	
	public static float[]	block_bottom(float x, float y, float z)
	{
		float[] array = {
				// Bottom face
				x + -0.5f, y - 0.5f, z + 0.5f,
				x + 0.5f, y - 0.5f, z + 0.5f,
				x + 0.5f, y - 0.5f, z + -0.5f,
				x + -0.5f, y - 0.5f, z + 0.5f,
				x + 0.5f, y - 0.5f, z + -0.5f,
				x + -0.5f, y - 0.5f, z + -0.5f
		};
		return (array);
	}
	
	public static float[]	texture_top(float id)
	{
		float[] array = {
				0, 0,	id,
		        1, 0,	id,
		        1, 1,	id,
				0, 0,	id,
		        1, 1,	id,
		        0, 1,	id,
		};
		return (array);
	}
	
	public static float[]	texture_back(float id)
	{
		float[] array = {
				1, 1,	id,
		        1, 0,	id,
		        0, 0,	id,
				1, 1,	id,
		        0, 0,	id,
		        0, 1,	id,
		};
		return (array);
	}
	
	public static float[]	texture_front(float id)
	{
		float[] array = {
				1, 1,	id,
		        1, 0,	id,
		        0, 1,	id,
		        1, 0,	id,
		        0, 0,	id,
		        0, 1,	id,
		};
		return (array);
	}
	
	public static float[]	texture_right(float id)
	{
		float[] array = {
				0, 0,	id,
		        1, 0,	id,
		        1, 1,	id,
				1, 1,	id,
		        0, 1,	id,
		        0, 0,	id,
		};
		return (array);
	}
	
	public static float[]	texture_left(float id)
	{
		float[] array = {
				1, 1,	id,
		        1, 0,	id,
		        0, 0,	id,
		        0, 0,	id,
		        0, 1,	id,
		        1, 1,	id,
		};
		return (array);
	}
	
	public static float[]	normal_front()
	{
		float[] array = {
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
		};
		return (array);
	}
	
	public static float[]	normal_back()
	{
		float[] array = {
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
				0.5f, 0.5f, 0,
		};
		return (array);
	}
	
	public static float[]	normal_left()
	{
		float[] array = {
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
		};
		return (array);
	}
	
	public static float[]	normal_right()
	{
		float[] array = {
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
				0, 0.5f, 0.5f,
		};
		return (array);
	}
	
	public static float[]	normal_top()
	{
		float[] array = {
				0.5f, 0, 0.5f,
				0.5f, 0, 0.5f,
				0.5f, 0, 0.5f,
				0.5f, 0, 0.5f,
				0.5f, 0, 0.5f,
				0.5f, 0, 0.5f,
		};
		return (array);
	}
	
	public static float[]	normal_bottom()
	{
		float[] array = {
				0, -1, 0,
		        0, -1, 0,
		        0, -1, 0,
		        0, -1, 0,
		        0, -1, 0,
		        0, -1, 0,
		};
		return (array);
	}
}
