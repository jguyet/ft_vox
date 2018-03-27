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
import java.util.Map.Entry;
import java.util.Random;

import org.joml.Vector3f;

import com.vox.Vox;
import com.vox.graphics.Camera;
import com.vox.graphics.GameObject;
import com.vox.graphics.Scene;
import com.vox.graphics.component.Chunk;
import com.vox.graphics.inputs.Keyboard;

public class GameScene extends Scene {
	
	private ArrayList<Chunk>			chunk_bach = new ArrayList<Chunk>();
	private Map<String, Chunk>			chunks = new HashMap<String, Chunk>();
	private Map<String, Chunk>			chunks_delatable = new HashMap<String, Chunk>();
	private ArrayList<Chunk>			objs_drawable = new ArrayList<Chunk>();
	private boolean						has_moved = true;
	private double						seed;
	private long						lasttime = 0;
	
	private static final int			LARGE = 2;
	
	public GameScene()
	{
		glfwSetCursorPos(Vox.vox.window, Vox.vox.screen.middleWidth, Vox.vox.screen.middleHeight);
		this.camera = new Camera(new Vector3f(5.0f, 5.0f, 5.0f), new Vector3f(37,316,0));
		this.camera.setProjection(50.0f, Vox.vox.screen.width, Vox.vox.screen.height, 0.1f, 600f);
		this.camera.buildFPSProjection();
		this.seed = new Random().nextInt();
		
		Chunk.build_shader();
		this.load_chunks(LARGE);
		while (this.chunk_bach.size() > 0 && Chunk.buffer_wait[0] == false)
		{
			Chunk c = chunk_bach.get(0);
			chunk_bach.remove(0);
			
			c.buffer_index = 0;
			c.pre_build_chunk();
			c.build_vao();
			c.vao_builded = true;
			this.add(c.gameObject);
			Chunk.buffer_wait[0] = false;
			//Chunk.buffer_wait[i] = true;
		}
		
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
		
		Map<String, Chunk>	chunks_d = new HashMap<String, Chunk>(this.chunks);
		
		for (int x = (fx - far); x < (fx + far); x++)
		{
			for (int z = (fz - far); z < (fz + far); z++)
			{
				for (int y = (0); y < (0 + 8); y++)
				{
					if (y < 0)
						continue ;
					int cx = Chunk.SIZE_WIDTH * x;
					int cz = Chunk.SIZE_WIDTH * z;
					int cy = Chunk.SIZE_HEIGHT * y;
					String key = "" + cx + "," + cz + "," + cy;
					
					if (chunks_d.containsKey(key))
						chunks_d.remove(key);
					if (chunks.containsKey(key))
						continue ;
					if (this.camera.transform.rotation.y > 130 && this.camera.transform.rotation.y < 230
							&& cz < (this.camera.transform.position.z - 50f))
						continue ;
					if ((this.camera.transform.rotation.y > 300 || this.camera.transform.rotation.y < 50)
							&& cz > (this.camera.transform.position.z + 50f))
						continue ;
					if ((this.camera.transform.rotation.y > 220 && this.camera.transform.rotation.y < 320)
							&& cx > (this.camera.transform.position.x + 50f))
						continue ;
					if ((this.camera.transform.rotation.y > 40 && this.camera.transform.rotation.y < 130)
							&& cx < (this.camera.transform.position.x - 50f))
						continue ;
					
					GameObject obj = new GameObject();
					obj.transform.position.x = cx;
					obj.transform.position.z = cz;
					obj.transform.position.y = cy;
					Chunk c = new Chunk();
					obj.addComponent(Chunk.class, c);
					
					chunks.put(key, c);
					chunk_bach.add(c);
				}
			}
		}
		this.chunks_delatable = chunks_d;
	}
	
	void								unload_chunks(int far)
	{
		//System.out.println("CHUNK A DELETE : " + this.chunks_delatable.size());
		
		Map<String, Chunk>	chunks_d = new HashMap<String, Chunk>(this.chunks_delatable);
		
		for (Entry<String, Chunk> entry : chunks_d.entrySet())
		{
			Chunk c = entry.getValue();
			
			c.gameObject.toDelete = true;
			this.chunks_delatable.remove(0);
			this.chunks.remove(entry.getKey());
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

	static long lastTimeo = 0;
	
	@Override
	public void draw() {
		this.move_camera();
		this.camera.buildFPSProjection();
		this._draw();
		updateFps();
		int i = 0;
		
		boolean has_draw = false;
		while (objs_drawable.size() > 0 && i < 2 && System.currentTimeMillis() - lastTimeo > (1000L / 30L))
		{
			Chunk c = objs_drawable.get(0);
			if (c == null)
				break ;
			c.build_vao();
			c.vao_builded = true;
			this.add(c.gameObject);
			objs_drawable.remove(c);
			has_draw = true;
			i++;
		}
		if (has_draw)
			lastTimeo = System.currentTimeMillis();
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
			if (has_moved)
			{
				this.load_chunks(LARGE);
				this.unload_chunks(LARGE);
			}
			
			for (int i = 0; i < Chunk.SIZE_STATIC_ARRAY; i++)
			{
				if (this.chunk_bach.size() > 0 && Chunk.buffer_wait[i] == false)
				{
					Chunk c = chunk_bach.get(0);
					chunk_bach.remove(c);
					
					c.buffer_index = i;
					c.pre_build_chunk();
					Chunk.buffer_wait[i] = true;
					this.objs_drawable.add(c);
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
