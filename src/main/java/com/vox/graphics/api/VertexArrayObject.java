package com.vox.graphics.api;

import java.nio.FloatBuffer;

import com.vox.graphics.component.Chunk;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexArrayObject {

	//VAO uid
	public int vao			= -1;
	
	//Vertex vbo
	public int vbo_vertex	= -1;
	//Texture positions vbo
	public int vbo_uv		= -1;
	//Normals vbo
	public int vbo_normal	= -1;
	
	private FloatBuffer vertex_array;
	private FloatBuffer uv_array;
	private FloatBuffer normal_array;
	
	private int triangles	= 0;
	
	public VertexArrayObject(FloatBuffer vertex_array, FloatBuffer uv_array, FloatBuffer normal_array)
	{
		this.vertex_array = vertex_array;
		this.uv_array = uv_array;
		this.normal_array = normal_array;
	}
	
	public void delete()
	{
		if (this.vao == -1)
			return ;
		glDeleteVertexArrays(this.vao);
		glDeleteBuffers(this.vbo_vertex);
		glDeleteBuffers(this.vbo_uv);
		glDeleteBuffers(this.vbo_normal);
	}
	
	public void add(float[] ... args)
	{
		if (args.length > 0)
		{
			this.vertex_array.put(args[0]);
			if (args[0].length > 0)
				this.triangles += args[0].length / 3;
		}
		if (args.length > 1)
			this.uv_array.put(args[1]);
		if (args.length > 2)
			this.normal_array.put(args[2]);
	}
	
	public void clear()
	{
		this.vertex_array.clear();
		this.uv_array.clear();
		this.normal_array.clear();
	}
	
	public void flip()
	{
		this.vertex_array.limit(this.vertex_array.position());
		this.uv_array.limit(this.uv_array.position());
		this.normal_array.limit(this.normal_array.limit());
		this.vertex_array.flip();
		this.uv_array.flip();
		this.normal_array.flip();
	}
	
	public void bind(int ... args)
	{	
		this.vao = glGenVertexArrays();
		glBindVertexArray(this.vao);
		if (args.length > 0)
		{
			this.vbo_vertex = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, this.vbo_vertex);
			glBufferData(GL_ARRAY_BUFFER, this.vertex_array, GL_STATIC_DRAW);
			glEnableVertexAttribArray(args[0]);
			glVertexAttribPointer(args[0], 3, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		if (args.length > 1)
		{
			this.vbo_uv = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, this.vbo_uv);
			glBufferData(GL_ARRAY_BUFFER, this.uv_array, GL_STATIC_DRAW);
			glEnableVertexAttribArray(args[1]);
			glVertexAttribPointer(args[1], 3, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		if (args.length > 2)
		{
			this.vbo_normal = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, this.vbo_normal);
			glBufferData(GL_ARRAY_BUFFER, this.normal_array, GL_STATIC_DRAW);
			glEnableVertexAttribArray(args[2]);
			glVertexAttribPointer(args[2], 3, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		glBindVertexArray(0);
	}
	
	public void draw(int type)
	{
		glBindVertexArray(this.vao);
		glDrawArrays(type, 0, this.triangles);
		glBindVertexArray(0);
	}
}
