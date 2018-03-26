package com.vox.graphics;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import com.vox.graphics.component.Chunk;

public abstract class Scene implements Runnable {
	
	public Camera					camera;
	
	private Map<Long, GameObject>	gameObjects = new HashMap<Long, GameObject>();
	
	protected Thread t;
	
	public Scene()
	{
		
	}
	
	public void add(GameObject obj)
	{
		this.gameObjects.put(obj.id, obj);
	}
	
	public boolean remove(GameObject obj)
	{
		if (this.gameObjects.containsKey(obj.id) == false)
			return false;
		this.gameObjects.remove(obj.id);
		return true;
	}
	
	protected void _draw()
	{
		if (Chunk.shader == null)
			Chunk.build_shader();
		glUseProgram(Chunk.shader.id);
		
		glUniformMatrix4fv(Chunk.projection_location, false, this.camera.projectionMatrix);
		glUniformMatrix4fv(Chunk.view_location, false, this.camera.viewMatrix);
		
		for (GameObject obj : gameObjects.values())
		{
			if (this.camera.transform.rotation.y > 130 && this.camera.transform.rotation.y < 230
					&& obj.transform.position.z < (this.camera.transform.position.z - 50f))
				continue ;
			if ((this.camera.transform.rotation.y > 300 || this.camera.transform.rotation.y < 50)
					&& obj.transform.position.z > (this.camera.transform.position.z + 50f))
				continue ;
			if ((this.camera.transform.rotation.y > 220 && this.camera.transform.rotation.y < 320)
					&& obj.transform.position.x > (this.camera.transform.position.x + 50f))
				continue ;
			if ((this.camera.transform.rotation.y > 40 && this.camera.transform.rotation.y < 130)
					&& obj.transform.position.x < (this.camera.transform.position.x - 50f))
				continue ;
			if (this.camera.transform.position.x > obj.transform.position.x + this.camera.zFar)
				continue ;
			if (this.camera.transform.position.x < obj.transform.position.x - this.camera.zFar)
				continue ;
			if (this.camera.transform.position.z > obj.transform.position.z + this.camera.zFar)
				continue ;
			if (this.camera.transform.position.z < obj.transform.position.z - this.camera.zFar)
				continue ;
			Chunk chunk = (Chunk)obj.getComponent(Chunk.class);
			
			FloatBuffer mMatrix = BufferUtils.createFloatBuffer(16);
	        new Matrix4f().translate(obj.transform.position)
	            //.rotateX(obj.transform.rotation.x)
	            //.rotateY(obj.transform.rotation.y)
	            //.rotateZ(obj.transform.rotation.z)
	            .get(mMatrix);
			
			if (chunk != null)
				chunk.draw(this.camera.projectionMatrix, this.camera.viewMatrix, mMatrix, this.camera.transform.position);
		}
		glUseProgram(0);
	}
	
	public abstract void draw();
}
