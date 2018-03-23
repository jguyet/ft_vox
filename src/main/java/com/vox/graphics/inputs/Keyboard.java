package com.vox.graphics.inputs;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class Keyboard {
	
	public static Keyboard keyboard = new Keyboard();
	
	private boolean[] keypressed = new boolean[301];
	
	public Keyboard()
	{
		for (int i = 0; i < 301; i++)
		{
			keypressed[i] = false;
		}
	}
	
	public void handle(int key, int scancode, int action, int mods)
	{
		//System.out.println("Key: " + key);
		if (key > 300)
			return ;
		keypressed[key] = action == GLFW_PRESS || action == GLFW_REPEAT ? true : false;
	}
	
	public boolean getKey(int key)
	{
		return (keypressed[key]);
	}
}
