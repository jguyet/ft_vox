package com.vox;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import com.vox.graphics.Scene;
import com.vox.graphics.Screen;
import com.vox.graphics.inputs.Keyboard;
import com.vox.graphics.inputs.Mouse;
import com.vox.loop.LoopMotor;
import com.vox.loop.MotorGraphics;
import com.vox.scenes.GameScene;

/**
 * Hello world!
 *
 */
public class Vox implements MotorGraphics
{
	public static Vox	vox;
	
	public Screen	screen;
	public Scene	scene;
	
	public long	window;
	public static boolean running;
	
	private LoopMotor motor;

	public Vox()
	{
		Vox.running = true;
		this.screen = new Screen(1700, 1000);
		this.scene = null;
	}
	
	public void destruct()
	{
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	private void build_context()
	{
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	}

	public void build_window()
	{
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints();
		//glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		//glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		glfwWindowHint(GLFW_DECORATED, GL_FALSE);
	    glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		this.build_context();
		window = glfwCreateWindow(this.screen.width, this.screen.height, "ft_vox", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
		glfwShowWindow(window);
		//glfwGetWindowAttrib(window, GLFW_VISIBLE);
		GL.createCapabilities();
	}
	
	public void build_inputs()
	{
		Keyboard.keyboard = new Keyboard();
		Mouse.mouse = new Mouse();
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			Keyboard.keyboard.handle(key, scancode, action, mods);
			if ((key == GLFW_KEY_ESCAPE || key == 290) && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		
		glfwSetCursorPosCallback(window, (window, x, y) -> {
			Mouse.mouse.handleCursorPosition((float)x, (float)y);
		});
		
	}

	public void loop()
	{
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_SCISSOR_TEST);
		//glEnable(GL_CULL_FACE);
		glClearColor(0.02f, 0.54f, 0.69f, 0.0f);
		
		this.motor = new LoopMotor(this);
		this.motor.start();
		this.running = false;
	}
	
	@Override
	public void graphicControllerLoop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void graphicRenderingLoop() {
		if (glfwWindowShouldClose(window))
		{
			motor.stop();
			return ;
		}
		glfwSwapBuffers(window);
		glfwPollEvents();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		if (Vox.vox.scene != null)
			Vox.vox.scene.draw();
	}

	public static void main(String[] args) {
		Vox.vox = new Vox();
		
		Vox.vox.build_window();
		Vox.vox.build_inputs();
		System.out.println("LWJGL version: " + Version.getVersion());
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		Factory.load_textures();
		Vox.vox.scene = new GameScene();
		
		Vox.vox.loop();
		Vox.vox.destruct();
	}
}
