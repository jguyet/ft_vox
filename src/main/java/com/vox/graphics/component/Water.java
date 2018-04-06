package com.vox.graphics.component;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import com.vox.Factory;
import com.vox.graphics.api.VertexArrayObject;
import com.vox.shader.WaterShader;
import com.vox.utils.Block;
import com.vox.utils.Sizeof;
import com.vox.utils.Texture;

public class Water extends Component{
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
	
	public static final int			SIZE_STATIC_ARRAY	= 1;
	public static final int			SIZE_WIDTH			= 16 * 40;
	public static final int			SIZE_HEIGHT			= 1;
	
	private FloatBuffer	vertexs_array;
	private FloatBuffer	uv_array;
	private FloatBuffer	normals_array;
	
	private Map<String, Block>		blocks = new HashMap<String, Block>();
	public VertexArrayObject		vertexArrayObject;
	public WaterShader				shader = null;
	
	public Water(WaterShader s)
	{
		this.vertexArrayObject = null;
		this.shader = s;
		
		ByteBuffer bb1 = BufferUtils.createByteBuffer(Sizeof.Float((6 * 18)));
		vertexs_array = bb1.asFloatBuffer();
		ByteBuffer bb4 = BufferUtils.createByteBuffer(Sizeof.Float((6 * 18)));
		uv_array = bb4.asFloatBuffer();
		ByteBuffer bb5 = BufferUtils.createByteBuffer(Sizeof.Float((6 * 18)));
		normals_array = bb5.asFloatBuffer();
	}
	
	public void destruct()
	{
		blocks.clear();
		if (this.vertexArrayObject != null)
			this.vertexArrayObject.delete();
	}
	
	public void pre_build_water()
	{
		this.vertexArrayObject = new VertexArrayObject(vertexs_array, uv_array, normals_array);
		build_arrays();
	}
	
	public void build_arrays()
	{	
		this.vertexArrayObject.clear();
		this.vertexArrayObject.add(Factory.plane(1500, 0,0,0),
				Factory.texture_top(-10),
				Factory.normal_top());
		this.vertexArrayObject.flip();
	}
	
	public void build_vao()
	{
		this.vertexArrayObject.bind(shader.vertex_location, shader.uv_location, shader.normal_location);
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
