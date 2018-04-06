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
import org.lwjgl.glfw.GLFWVidMode;
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
	
	public int seed;

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
		glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
	}

	public void build_window(boolean fullscreen)
	{
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		//glfwWindowHint(GLFW_DECORATED, GL_FALSE);
	    glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
	    //glfwWindowHint(GLFW_AUTO_ICONIFY, GL_TRUE);
		this.build_context();
		
		long monitor = 0;
		
		if (fullscreen)
		{
			monitor = glfwGetPrimaryMonitor();
			GLFWVidMode Mode = glfwGetVideoMode(monitor);
		    glfwWindowHint(GLFW_RED_BITS, Mode.redBits());
		    glfwWindowHint(GLFW_GREEN_BITS, Mode.greenBits());
		    glfwWindowHint(GLFW_BLUE_BITS, Mode.blueBits());
		    glfwWindowHint(GLFW_ALPHA_BITS, Mode.blueBits());
		    glfwWindowHint(GLFW_REFRESH_RATE, Mode.refreshRate());
		    this.screen.height = Mode.height();
		    this.screen.width = Mode.width();
		}
		window = glfwCreateWindow(this.screen.width, this.screen.height, "ft_vox", monitor, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
		glfwShowWindow(window);
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
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.90f, 0.90f, 0.90f, 0.0f);
		
		this.motor = new LoopMotor(this);
		this.motor.start();
		this.running = false;
	}
	
	@Override
	public void graphicControllerLoop() {
		// TODO Auto-generated method stub
		if (Vox.vox.scene != null)
			Vox.vox.scene.update();
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
		
		boolean fullscreen = false;
		Vox.vox = new Vox();
		for (String arg : args)
		{
			if (arg.contains("-seed:") && arg.length() > "-seed:".length())
			{
				Vox.vox.seed = 0;
				
				try {
					Vox.vox.seed = Integer.parseInt(arg.split("\\-seed\\:")[1]);
				} catch (Exception e)
				{
					System.out.println("arg[0] seed is not a number");
				}
			}
			else if (arg.equalsIgnoreCase("-fullscreen"))
			{
				fullscreen = true;
			}
		}
		System.out.println("seed = " + Vox.vox.seed);
		Vox.vox.build_window(fullscreen);
		Vox.vox.build_inputs();
		System.out.println("LWJGL version: " + Version.getVersion());
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		Factory.load_textures();
		Factory.load_shaders();
		Vox.vox.scene = new GameScene();
		
		Vox.vox.loop();
		Vox.vox.destruct();
	}
}
