package com.vox.utils;

import org.joml.Vector3f;

import com.vox.graphics.component.Chunk;

public class Block {
	
	public Vector3f		position;
	public float		texture_top = 0;
	public float		texture_side = 0;
	public float		texture_bottom = 0;
	public Chunk		chunk;
	
	public Block(Chunk c, float x, float y, float z)
	{
		this.chunk = c;
		this.position = new Vector3f(x, y, z);
	}
	
	public Block(Chunk c, Vector3f position)
	{
		this.chunk = c;
		this.position = position;
	}
}
