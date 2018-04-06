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
import com.vox.graphics.component.Component;
import com.vox.graphics.component.Water;

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
	}
	
	protected void _draw()
	{
		for (GameObject obj : gameObjects.values())
		{
			if (obj.toDelete)
			{
				objs_deletes.add(obj);
				continue ;
			}
			Component c = obj.getComponent(Chunk.class);
			
			if (c instanceof Chunk)
			{
				Chunk chunk = (Chunk)c;
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
				chunk.draw(this.camera.projectionMatrix, this.camera.viewMatrix, obj.getMatrix(this.camera), this.camera.transform.position);
			}
//			else if (c instanceof Water)
//			{
//				Water water = (Water)c;
//				
//				water.draw(this.camera.projectionMatrix, this.camera.viewMatrix, obj.getMatrix(this.camera), this.camera.transform.position);
//			}
		}
		
		int i = 0;
		while (objs_deletes.size() > 0 && i < 20)
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
