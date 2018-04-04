package com.vox.graphics;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3f;
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
	private ArrayList<GameObject>	objs_deletes = new ArrayList<GameObject>();
	
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
		obj.toDelete = true;
		return (true);
//		if (this.gameObjects.containsKey(obj.id) == false)
//			return false;
//		this.gameObjects.remove(obj.id);
//		return true;
	}
	
	protected void _draw()
	{	
		if (Chunk.shader == null)
			Chunk.build_shader();
		glUseProgram(Chunk.shader.id);
		glUniformMatrix4fv(Chunk.projection_location, false, this.camera.projectionMatrix);
		glUniformMatrix4fv(Chunk.view_location, false, this.camera.viewMatrix);
		glUniform3f(glGetUniformLocation(Chunk.shader.id, "light_worldspace"), this.camera.transform.position.x + 100, 50, this.camera.transform.position.z + 100);
		
		for (GameObject obj : gameObjects.values())
		{
			if (obj.toDelete)
			{
				objs_deletes.add(obj);
				continue ;
			}
			Chunk chunk = (Chunk)obj.getComponent(Chunk.class);
			
			if (chunk.buffer_index == -1)
			{
				if (this.camera.transform.rotation.y > 130 && this.camera.transform.rotation.y < 230
						&& obj.transform.position.z < (this.camera.transform.position.z - (this.camera.transform.position.y)))
					continue ;
				if ((this.camera.transform.rotation.y > 300 || this.camera.transform.rotation.y < 50)
						&& obj.transform.position.z > (this.camera.transform.position.z + (this.camera.transform.position.y)))
					continue ;
				if ((this.camera.transform.rotation.y > 220 && this.camera.transform.rotation.y < 320)
						&& obj.transform.position.x > (this.camera.transform.position.x + (this.camera.transform.position.y)))
					continue ;
				if ((this.camera.transform.rotation.y > 40 && this.camera.transform.rotation.y < 130)
						&& obj.transform.position.x < (this.camera.transform.position.x - (this.camera.transform.position.y)))
					continue ;
			}
			chunk.draw(this.camera.projectionMatrix, this.camera.viewMatrix, obj.getMatrix(), this.camera.transform.position);
		}
		glUseProgram(0);
		
		int i = 0;
		while (objs_deletes.size() > 0 && i < 5)
		{
			GameObject obj = objs_deletes.get(0);
			
			Chunk c = (Chunk)obj.getComponent(Chunk.class);
			
			if (c != null)
				c.destruct();
			
			//System.out.println("DELETE : " + obj.id);
			this.gameObjects.remove(obj.id);
			
			objs_deletes.remove(0);
			i++;
		}
		objs_deletes.clear();
	}
	
	public abstract void draw();
	
	public abstract void update();
}
