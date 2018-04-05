package com.vox.shader;

import com.vox.utils.Util;

public abstract class Shader {
	
	public int id = 0;
	
	public Shader(String vertex_path, String fragment_path)
	{
		try {
			this.id = Util.createShaderProgram(Util.read(vertex_path), Util.read(fragment_path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void bind();
}
