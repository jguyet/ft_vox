package com.vox.scenes;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.joml.Vector3f;

import com.vox.Vox;
import com.vox.graphics.Camera;
import com.vox.graphics.GameObject;
import com.vox.graphics.Scene;
import com.vox.graphics.component.Chunk;
import com.vox.graphics.inputs.Keyboard;

public class GameScene extends Scene {
	
	private ArrayList<Vector3f>			chunk_bach = new ArrayList<Vector3f>();
	private static Map<String, Boolean>	chunks = new HashMap<String, Boolean>();
	private ArrayList<GameObject>		objs = new ArrayList<GameObject>();
	private boolean						has_moved = true;
	private double						seed;
	private long						lasttime = 0;
	
	public GameScene()
	{
		glfwSetCursorPos(Vox.vox.window, Vox.vox.screen.middleWidth, Vox.vox.screen.middleHeight);
		this.camera = new Camera(new Vector3f(5.0f, 5.0f, 5.0f), new Vector3f(37,316,0));
		this.camera.setProjection(50.0f, Vox.vox.screen.width, Vox.vox.screen.height, 0.1f, 400f);
		this.camera.buildFPSProjection();
		this.seed = new Random().nextInt();
		
		this.load_chunks(17);
		
		this.t = new Thread(this);
		this.t.setPriority(Thread.MIN_PRIORITY);
		this.t.setDaemon(false);
		this.t.start();
	}
	
	void								load_chunks(int far)
	{
		int fx = (int)Math.floor(this.camera.transform.position.x / Chunk.SIZE_WIDTH);
		int fz = (int)Math.floor(this.camera.transform.position.z / Chunk.SIZE_WIDTH);
		int fy = (int)Math.floor(this.camera.transform.position.y / Chunk.SIZE_HEIGHT);
		for (int x = (fx - far); x < (fx + far); x++)
		{
			for (int z = (fz - far); z < (fz + far); z++)
			{
				for (int y = (0 - 1); y < (0 + 6); y++)
				{
					if (y < 0)
						continue ;
					int cx = Chunk.SIZE_WIDTH * x;
					int cz = Chunk.SIZE_WIDTH * z;
					int cy = Chunk.SIZE_HEIGHT * y;
					String key = "" + cx + "," + cz + "," + cy;
					
					if (chunks.containsKey(key))
						continue ;
					chunk_bach.add(new Vector3f(cx, cy, cz));
					chunks.put(key, true);
					//System.out.println("SALUT" + cx + " " + cy + " " + cz);
				}
			}
		}
	}
	
	void								load_chunks_line(int far, int large, int size)
	{
		load_chunks(size);
		
		int fx = (int)Math.floor(this.camera.transform.position.x / Chunk.SIZE_WIDTH);
		int fz = (int)Math.floor(this.camera.transform.position.z / Chunk.SIZE_WIDTH);
		int fy = (int)Math.floor(this.camera.transform.position.y / Chunk.SIZE_HEIGHT);
		for (int x = (fx - far); x < (fx + far); x++)
		{
			for (int z = (fz - large); z < (fz + large); z++)
			{
				for (int y = (0 - 1); y < (0 + 6); y++)
				{
					if (y < 0)
						continue ;
					int cx = Chunk.SIZE_WIDTH * x;
					int cz = Chunk.SIZE_WIDTH * z;
					int cy = Chunk.SIZE_HEIGHT * y;
					String key = "" + cx + "," + cz + "," + cy;
					
					if (chunks.containsKey(key))
						continue ;
					chunk_bach.add(new Vector3f(cx, cy, cz));
					chunks.put(key, true);
					//System.out.println("SALUT" + cx + " " + cy + " " + cz);
				}
			}
		}
		
		for (int x = (fx - large); x < (fx + large); x++)
		{
			for (int z = (fz - far); z < (fz + far); z++)
			{
				for (int y = (0 - 1); y < (0 + 6); y++)
				{
					if (y < 0)
						continue ;
					int cx = Chunk.SIZE_WIDTH * x;
					int cz = Chunk.SIZE_WIDTH * z;
					int cy = Chunk.SIZE_HEIGHT * y;
					String key = "" + cx + "," + cz + "," + cy;
					
					if (chunks.containsKey(key))
						continue ;
					chunk_bach.add(new Vector3f(cx, cy, cz));
					chunks.put(key, true);
					//System.out.println("SALUT" + cx + " " + cy + " " + cz);
				}
			}
		}
	}
	
	void								unload_chunks(int far)
	{
//		int fx = (int)Math.floor(this.camera.transform.position.x / Chunk.SIZE_WIDTH);
//		int fz = (int)Math.floor(this.camera.transform.position.z / Chunk.SIZE_WIDTH);
//		int fy = (int)Math.floor(this.camera.transform.position.y / Chunk.SIZE_HEIGHT);
//		
//		for (Chunk c : Chunk.chunks.values())
//		{
//			
//		}
	}
	
	void								move_camera()
	{
		has_moved = false;
		if (Keyboard.keyboard.getKey(GLFW_KEY_W)) {//UP
			has_moved = true;
			this.camera.move(new Vector3f(0, 0, 5));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_D)) {//RIGHT
			has_moved = true;
			this.camera.move(new Vector3f(5, 0, 0));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_A)) {//LEFT
			has_moved = true;
			this.camera.move(new Vector3f(-5, 0, 0));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_S)) {//DOWN
			has_moved = true;
			this.camera.move(new Vector3f(0, 0, -5));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_SPACE)) {//DOWN
			has_moved = true;
			this.camera.transform.position.y += 2f;
		}
	}

	@Override
	public void draw() {
		if (objs.size() > 0 && System.currentTimeMillis() > lasttime + (1000L / 120L))
		{
			GameObject obj = objs.iterator().next();
			
			Chunk c = (Chunk)obj.getComponent(Chunk.class);
			c.build_vao();
			c.vao_builded = true;
			this.add(obj);
			objs.remove(obj);
			lasttime = System.currentTimeMillis();
		}
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

	@Override
	public void run() {
		while (Vox.running == true)
		{
			if (has_moved && System.currentTimeMillis() > lasttime + (1000L / 20L))
			{
				//this.load_chunks_line(10, 4, 5);
				this.load_chunks(17);
			}
			
			for (int i = 0; i < Chunk.SIZE_STATIC_ARRAY; i++)
			{
				if (this.chunk_bach.size() > 0 && Chunk.buffer_wait[i] == false)
				{
					Vector3f v = chunk_bach.iterator().next();
					chunk_bach.remove(v);
					GameObject obj = new GameObject();
					obj.transform.position.x = v.x;
					obj.transform.position.z = v.z;
					obj.transform.position.y = v.y;
					//Chunk.wait = true;
					Chunk c = new Chunk();
					
					c.buffer_index = i;
					obj.addComponent(Chunk.class, c);
					c.pre_build_chunk();
					Chunk.buffer_wait[i] = true;
					this.objs.add(obj);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
