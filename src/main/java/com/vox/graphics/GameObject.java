package com.vox.graphics;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import com.vox.graphics.component.Component;
import com.vox.graphics.math.Transform;

public class GameObject {
	
	public static long					uid;
	
	public Transform					transform;
	public long							id;
	
	public boolean						toDelete = false;
	
	
	private Map<Class<?>, Component>	components = new HashMap<Class<?>, Component>();
	private Vector3f					lastPosition;
	private FloatBuffer					matrix;
	
	public GameObject()
	{
		this.lastPosition = new Vector3f(0,0,0);
		this.matrix = BufferUtils.createFloatBuffer(16);
		this.id = GameObject.uid++;
		this.transform = new Transform();
	}
	
	public void addComponent(Class<?> key, Component c)
	{	
		if (components.containsKey(key))
			components.remove(key);
		c.gameObject = this;
		components.put(key, c);
	}
	
	public Component getComponent(Class<?> key)
	{
		if (components.containsKey(key))
			return (components.get(key));
		return (null);
	}
	
	public FloatBuffer	getMatrix(Camera c)
	{
		Vector3f position = new Vector3f(this.transform.position.x - c.position.x, this.transform.position.y, this.transform.position.z - c.position.z);
		
		if (lastPosition.equals(position))
			return (this.matrix);
		lastPosition.x = position.x;
		lastPosition.y = position.y;
		lastPosition.z = position.z;
		new Matrix4f().translate(position)
        //.rotateX(obj.transform.rotation.x)
        //.rotateY(obj.transform.rotation.y)
        //.rotateZ(obj.transform.rotation.z)
        .get(this.matrix);
		return (this.matrix);
	}
}
