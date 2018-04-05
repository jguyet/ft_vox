package com.vox.shader;

import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class ChunkShader extends Shader {

	public int		vertex_location;
	public int		uv_location;
	public int		normal_location;
	public int		color_location;
	public int		projection_location;
	public int		view_location;
	public int		model_location;
	public int[]	texture_diffuse_location = new int[16];//16 max number of textures

	public ChunkShader(String vertex_path, String fragment_path) {
		super(vertex_path, fragment_path);
		
		vertex_location = glGetAttribLocation(this.id, "a_v");
		normal_location = glGetAttribLocation(this.id, "a_n");
		uv_location = glGetAttribLocation(this.id, "a_uv");
		color_location = glGetAttribLocation(this.id, "a_color");
		projection_location = glGetUniformLocation(this.id, "P");
		view_location = glGetUniformLocation(this.id, "V");
		model_location = glGetUniformLocation(this.id, "M");
		for (int i = 0; i < 16; i++) {
			texture_diffuse_location[i] = glGetUniformLocation(this.id, "u_texture_diffuse[" + i + "]");
		}
	}

	@Override
	public void bind() {
		
	}
	
}
