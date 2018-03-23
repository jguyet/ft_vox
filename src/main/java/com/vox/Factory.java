package com.vox;

import org.joml.Vector3f;

import com.vox.utils.Texture;

public class Factory {
	
	public static void load_textures()
	{
		//Texture.loadTexture3D("grass", 512, 512, "assets/textures/grass_top.jpg", "assets/textures/grass_bottom.jpg", "assets/textures/grass_side.jpg");
		
		Texture.loadTexture2D("grass_top", "assets/textures/grass_top.jpg");
		Texture.loadTexture2D("grass_side", "assets/textures/grass_side.jpg");
		Texture.loadTexture2D("grass_bottom", "assets/textures/grass_bottom.jpg");
	}

	public static float[]	block_front(float x, float y, float z)
	{
		float[] array = {
				// Front face
				x + -0.5f, y + -0.5f, z + 0.5f,
				x + -0.5f, y + 0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f,
				x + -0.5f, y + -0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f,
				x + 0.5f, y + -0.5f, z + 0.5f
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
				x + -0.5f, y + -0.5f, z + 0.5f,
				x + -0.5f, y + 0.5f, z + 0.5f,
		        x + -0.5f, y + 0.5f, z + -0.5f,
		        x + -0.5f, y + -0.5f, z + 0.5f,
		        x + -0.5f, y + -0.5f, z + -0.5f,
		        x + -0.5f, y + 0.5f, z + -0.5f
		};
		return (array);
	}
	
	public static float[]	block_right(float x, float y, float z)
	{
		float[] array = {
				// Right face
				x + 0.5f, y + -0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + -0.5f,
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
				x + -0.5f, y + 0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + -0.5f,
				x + -0.5f, y + 0.5f, z + 0.5f,
				x + 0.5f, y + 0.5f, z + -0.5f,
				x + -0.5f, y + 0.5f, z + -0.5f
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
	
	public static float[]	texture_front()
	{
		float[] array = {
				0, 0,	0,//texture 0
		        1, 0,	0,
		        1, 1,	0,
				0, 0,	0,
		        1, 1,	0,
		        0, 1,	0,
		};
		return (array);
	}
	
	public static float[]	texture_side()
	{
		float[] array = {
				0, 0,	2.1f,//texture 0
		        1, 0,	2.1f,
		        1, 1,	2.1f,
				0, 0,	2.1f,
		        1, 1,	2.1f,
		        0, 1,	2.1f,
		};
		return (array);
	}
	
	public static float[]	normal_front()
	{
		float[] array = {
				0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		};
		return (array);
	}
	
	public static float[]	normal_back()
	{
		float[] array = {
				0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		        0, 0, 1,
		};
		return (array);
	}
	
	public static float[]	normal_left()
	{
		float[] array = {
				1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		};
		return (array);
	}
	
	public static float[]	normal_right()
	{
		float[] array = {
				1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		        1, 0, 0,
		};
		return (array);
	}
	
	public static float[]	normal_top()
	{
		float[] array = {
				0, 1, 0,
		        0, 1, 0,
		        0, 1, 0,
		        0, 1, 0,
		        0, 1, 0,
		        0, 1, 0,
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
