package com.vox.scenes;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

import org.joml.Vector3f;

import com.vox.Vox;
import com.vox.graphics.Camera;
import com.vox.graphics.GameObject;
import com.vox.graphics.Scene;
import com.vox.graphics.component.Chunk;
import com.vox.graphics.inputs.Keyboard;

public class GameScene extends Scene {
	
	public static int far_chunk = 15;
	private ArrayList<Vector3f> chunk_bach = new ArrayList<Vector3f>();
	private static Map<String, Boolean>	chunks = new HashMap<String, Boolean>();
	private boolean has_moved = true;
	
	public GameScene()
	{
		glfwSetCursorPos(Vox.vox.window, Vox.vox.screen.middleWidth, Vox.vox.screen.middleHeight);
		this.camera = new Camera(new Vector3f(5.0f, 5.0f, 5.0f), new Vector3f(37,316,0));
		this.camera.setProjection(45.0f, 1280.0f, 740.0f, 0.1f, 1000f);
		this.camera.buildFPSProjection();
		load_chunks(15);
	}
	
	void								add_chunk(int number)
	{
		ArrayList<Vector3f> chunk_a = new ArrayList<Vector3f>();
		
		for (int i = 0; i < number; i++)
		{
			if (chunk_bach.size() > 0)
			{
				chunk_a.add(chunk_bach.get(0));
				chunk_bach.remove(0);
			}
		}
		for (Vector3f v : chunk_a)
		{
			GameObject obj = new GameObject();
			obj.transform.position.x = v.x;
			obj.transform.position.z = v.z;
			obj.transform.position.y = v.y;
			Chunk c = new Chunk();
			obj.addComponent(Chunk.class, c);
			c.build_chunk();
			this.add(obj);
		}
	}
	
	void								load_chunks(int far)
	{
		int fx = (int)Math.floor((int)this.camera.transform.position.x / Chunk.SIZE_WIDTH);
		int fz = (int)Math.floor((int)this.camera.transform.position.z / Chunk.SIZE_WIDTH);
		for (int x = (fx - far_chunk); x < (fx + far_chunk); x++)
		{
			for (int z = (fz - far); z < (fz + far); z++)
			{
				int cx = Chunk.SIZE_WIDTH * x;
				int cz = Chunk.SIZE_WIDTH * z;
				String key = "" + cx + "," + cz;
				
				if (chunks.containsKey(key))
					continue ;
				chunk_bach.add(new Vector3f(cx, 0, cz));
				chunks.put(key, true);
			}
		}
	}
	
	void								move_camera()
	{
		has_moved = false;
		if (Keyboard.keyboard.getKey(GLFW_KEY_W)) {//UP
			has_moved = true;
			this.camera.move(new Vector3f(0, 0, 20));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_D)) {//RIGHT
			has_moved = true;
			this.camera.move(new Vector3f(20, 0, 0));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_A)) {//LEFT
			has_moved = true;
			this.camera.move(new Vector3f(-20, 0, 0));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_S)) {//DOWN
			has_moved = true;
			this.camera.move(new Vector3f(0, 0, -20));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_SPACE)) {//DOWN
			has_moved = true;
			this.camera.transform.position.y += 2f;
		}
	}

	@Override
	public void draw() {
		//System.out.println(camera.transform.rotation.y);
		if (has_moved)
			load_chunks(10);
		//if (fps > 60)
		this.add_chunk(1);
		this.move_camera();
		this.camera.buildFPSProjection();
		this._draw();
		updateFps();
	}
	
	static long lastTime = 0;
	static int fpsCount = 0;
	static int fps = 0;
	
	void						updateFps()
	{
		if (lastTime == 0 || System.currentTimeMillis() > (long)(lastTime + 1000L)) {
			lastTime = System.currentTimeMillis();
			fps = fpsCount;
			fpsCount = 0;
			System.out.println("FPS : " + fps);
		}
		fpsCount++;
	}
	
}
