package com.vox.graphics;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import com.vox.graphics.component.Chunk;

public abstract class Scene {
	
	public Camera					camera;
	
	private Map<Long, GameObject>	gameObjects = new HashMap<Long, GameObject>();
	
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
		for (GameObject obj : gameObjects.values())
		{
			if (this.camera.transform.rotation.y > 130 && this.camera.transform.rotation.y < 230
					&& obj.transform.position.z < (this.camera.transform.position.z - 10f))
				continue ;
			if ((this.camera.transform.rotation.y > 300 || this.camera.transform.rotation.y < 50)
					&& obj.transform.position.z > (this.camera.transform.position.z + 10f))
				continue ;
			if ((this.camera.transform.rotation.y > 220 && this.camera.transform.rotation.y < 320)
					&& obj.transform.position.x > (this.camera.transform.position.x + 10f))
				continue ;
			if ((this.camera.transform.rotation.y > 40 && this.camera.transform.rotation.y < 130)
					&& obj.transform.position.x < (this.camera.transform.position.x - 10f))
				continue ;
//			if (this.camera.transform.position.x > obj.transform.position.x + 200)
//				continue ;
//			if (this.camera.transform.position.x < obj.transform.position.x - 200)
//				continue ;
//			if (this.camera.transform.position.z > obj.transform.position.z + 200)
//				continue ;
//			if (this.camera.transform.position.z < obj.transform.position.z - 200)
//				continue ;
			Chunk chunk = (Chunk)obj.getComponent(Chunk.class);
			
			FloatBuffer mMatrix = BufferUtils.createFloatBuffer(16);
	        new Matrix4f()//.translate(obj.transform.position)
	            //.rotateX(obj.transform.rotation.x)
	            //.rotateY(obj.transform.rotation.y)
	            //.rotateZ(obj.transform.rotation.z)
	            .get(mMatrix);
			
			if (chunk != null)
				chunk.draw(this.camera.projectionMatrix, this.camera.viewMatrix, mMatrix, this.camera.transform.position);
		}
	}
	
	public abstract void draw();
}
