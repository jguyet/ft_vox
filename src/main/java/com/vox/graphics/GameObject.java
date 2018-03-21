package com.vox.graphics;

import java.util.HashMap;
import java.util.Map;

import com.vox.graphics.component.Component;
import com.vox.graphics.math.Transform;

public class GameObject {
	
	public static long					uid;
	
	public Transform					transform;
	public long							id;
	
	private Map<Class<?>, Component>	components = new HashMap<Class<?>, Component>();
	
	public GameObject()
	{
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
}
