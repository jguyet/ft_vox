package com.vox.graphics.inputs;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import org.joml.Vector2f;

import com.vox.Vox;

public class Mouse {
	
	public static Mouse mouse = new Mouse();
	
	public Vector2f lastPosition;
	public Vector2f position;
	
	public Mouse()
	{
		this.lastPosition = new Vector2f(0,0);
		this.position = new Vector2f(0,0);
	}
	
	public void handleCursorPosition(float x, float y)
	{
		float mw = Vox.vox.screen.middleWidth;
		float mh = Vox.vox.screen.middleHeight;
		this.lastPosition.x = this.position.x;
		this.lastPosition.y = this.position.y;
		this.position.x = x;
		this.position.y = y;
		if (this.lastPosition.x == 0 && this.lastPosition.y == 0)
			return ;
		if (Vox.vox.scene == null)
			return ;
		Vox.vox.scene.camera.mouseMove(this.lastPosition, this.position);
		//glfwSetCursorPos(Vox.vox.window, Vox.vox.screen.middleWidth, Vox.vox.screen.middleHeight);
	}
}
