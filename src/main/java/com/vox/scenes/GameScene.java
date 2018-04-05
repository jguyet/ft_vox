package com.vox.scenes;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.joml.Vector3f;

import com.vox.Factory;
import com.vox.Vox;
import com.vox.graphics.Camera;
import com.vox.graphics.GameObject;
import com.vox.graphics.Scene;
import com.vox.graphics.component.Chunk;
import com.vox.graphics.inputs.Keyboard;
import com.vox.shader.ChunkShader;

public class GameScene extends Scene {
	
	private ArrayList<Chunk>			chunk_bach = new ArrayList<Chunk>();
	private Map<String, Chunk>			chunks = new HashMap<String, Chunk>();
	private Map<String, Chunk>			chunks_delatable = new HashMap<String, Chunk>();
	private ArrayList<Chunk>			objs_drawable = new ArrayList<Chunk>();
	private boolean						has_moved = true;
	private double						seed;
	private long						lasttime = 0;
	
	private static final int			LARGE = 10;
	
	public GameScene()
	{
		glfwSetCursorPos(Vox.vox.window, Vox.vox.screen.middleWidth, Vox.vox.screen.middleHeight);
		this.camera = new Camera(new Vector3f(5000.0f, 5.0f, 5000.0f), new Vector3f(37,316,0));
		this.camera.setProjection(50.0f, Vox.vox.screen.width, Vox.vox.screen.height, 0.1f, 1000f);
		this.camera.buildFPSProjection();
		this.seed = new Random().nextInt();
		
		this.load_chunks(LARGE * 2, LARGE * 2);
		while (this.chunk_bach.size() > 0 && Chunk.buffer_wait[0] == false)
		{
			Chunk c = chunk_bach.get(0);
			chunk_bach.remove(0);
			
			c.pre_build_chunk(0);
			c.build_vao();
			c.vao_builded = true;
			this.add(c.gameObject);
			Chunk.buffer_wait[0] = false;
		}
		
		this.t = new Thread(this);
		this.t.setPriority(Thread.MIN_PRIORITY);
		this.t.start();
	}
	
	void								load_chunks(int far_load, int far_delete)
	{
		int far = far_load > far_delete ? far_load : far_delete;
		int fx = (int)Math.floor(this.camera.transform.position.x / Chunk.SIZE_WIDTH);
		int fz = (int)Math.floor(this.camera.transform.position.z / Chunk.SIZE_WIDTH);
		
		Map<String, Chunk>	chunks_d = new HashMap<String, Chunk>(this.chunks);
		
		for (int f = (0 - far); f < far; f++)
		{
			for (int x = (fx - f); x < (fx + f); x++)
			{
				for (int z = (fz - f); z < (fz + f); z++)
				{
					int cx = Chunk.SIZE_WIDTH * x;
					int cz = Chunk.SIZE_WIDTH * z;
					int cy = 0;
					String key = "" + cx + "," + cz + "," + cy;
					
					if (this.camera.transform.rotation.y > 130 && this.camera.transform.rotation.y < 230
							&& cz < (this.camera.transform.position.z - (this.camera.transform.position.y)))
						continue ;
					if ((this.camera.transform.rotation.y > 300 || this.camera.transform.rotation.y < 50)
							&& cz > (this.camera.transform.position.z + (this.camera.transform.position.y)))
						continue ;
					if ((this.camera.transform.rotation.y > 220 && this.camera.transform.rotation.y < 320)
							&& cx > (this.camera.transform.position.x + (this.camera.transform.position.y)))
						continue ;
					if ((this.camera.transform.rotation.y > 40 && this.camera.transform.rotation.y < 130)
							&& cx < (this.camera.transform.position.x - (this.camera.transform.position.y)))
						continue ;
					if (far_load < f)
					{
						if (chunks_d.containsKey(key))
							chunks_d.remove(key);
						continue ;
					}
					if (chunks_d.containsKey(key))
						chunks_d.remove(key);
					if (chunks.containsKey(key))
						continue ;
					
					GameObject obj = new GameObject();
					obj.transform.position.x = cx;
					obj.transform.position.z = cz;
					obj.transform.position.y = cy;
					Chunk c = new Chunk((ChunkShader)Factory.shaders.get("ChunkShader"));
					obj.addComponent(Chunk.class, c);
					
					chunks.put(key, c);
					chunk_bach.add(c);
				}
			}
		}
		final Vector3f cameraPosition = new Vector3f(this.camera.transform.position.x, this.camera.transform.position.y, this.camera.transform.position.z);
		
		Collections.sort(chunk_bach, new Comparator<Chunk>() {
		    public int compare(Chunk c1, Chunk c2) {
		        int x1 = (int) Math.abs(c1.gameObject.transform.position.x - cameraPosition.x);
		        int x2 = (int) Math.abs(c2.gameObject.transform.position.x - cameraPosition.x);
		        
		        int z1 = (int) Math.abs(c1.gameObject.transform.position.z - cameraPosition.z);
		        int z2 = (int) Math.abs(c2.gameObject.transform.position.z - cameraPosition.z);
		        
		        if ((x1 < x2) && (z1 < z2))
		        	return 1;
		        return 0;
		    }
		});
		
		this.chunks_delatable = chunks_d;
		unload_chunks();
	}
	
	void								unload_chunks()
	{
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
		if (Keyboard.keyboard.getKey(GLFW_KEY_W)) {//UP
			has_moved = true;
			this.camera.move(new Vector3f(0, 0, 30));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_D)) {//RIGHT
			has_moved = true;
			this.camera.move(new Vector3f(30, 0, 0));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_A)) {//LEFT
			has_moved = true;
			this.camera.move(new Vector3f(-30, 0, 0));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_S)) {//DOWN
			has_moved = true;
			this.camera.move(new Vector3f(0, 0, -30));
		}
		if (Keyboard.keyboard.getKey(GLFW_KEY_SPACE)) {//DOWN
			has_moved = true;
			this.camera.transform.position.y += 2f;
		}
		
		//if (this.camera.transform.position.y > 256f)
		//	this.camera.transform.position.y = 256f;
		if (this.camera.transform.position.y < 0f)
			this.camera.transform.position.y = 0f;
	}

	static long lastTimeo = 0;
	
	@Override
	public void draw() {
		this.camera.buildFPSProjection();
		this._draw();
		updateFps();
		
		boolean has_draw = false;
		int max = 0;
		while (objs_drawable.size() > 0)// && max < 40 && System.currentTimeMillis() - lastTimeo > (1000L / 30L))
		{
			Chunk c = objs_drawable.get(0);
			if (c == null)
				break ;
			if (c.vao_builded == false)
				c.build_vao();
			this.add(c.gameObject);
			objs_drawable.remove(c);
			has_draw = true;
			max++;
		}
		if (has_draw) {
			lastTimeo = System.currentTimeMillis();
		}
	}
	
	@Override
	public void update() {
		this.move_camera();
		
		updateFFps();
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
			System.out.println("FPS          : " + fps);
		}
		fpsCount++;
	}
	
	
	static long lastfTime = 0;
	static int ffpsCount = 0;
	static int ffps = 0;
	
	void						updateFFps()
	{
		if (lastfTime == 0 || System.currentTimeMillis() > (long)(lastfTime + 1000L)) {
			lastfTime = System.currentTimeMillis();
			ffps = ffpsCount;
			ffpsCount = 0;
			System.out.println("FFPS          : " + ffps);
		}
		ffpsCount++;
	}
	
	static long lastpTime = 0;
	static int pfpsCount = 0;
	static int pfps = 0;
	
	void						updatePFps()
	{
		if (lastpTime == 0 || System.currentTimeMillis() > (long)(lastpTime + 1000L)) {
			lastpTime = System.currentTimeMillis();
			pfps = pfpsCount;
			pfpsCount = 0;
			System.out.println("PFPS          : " + pfps);
		}
		pfpsCount++;
	}

	@Override
	public void run() {
		int ll = 0;
		
		while (Vox.running == true)
		{
			this.load_chunks(LARGE, LARGE * 2);
			if (this.chunk_bach.size() > 0)
			{
				ll = LARGE;
			}
			else {
				if (LARGE + ll < LARGE * 2)
					ll++;
				this.load_chunks(LARGE + ll, LARGE * 2);
			}
			
			int i = 0;
			while (i < Chunk.SIZE_STATIC_ARRAY)
			{
				if (this.chunk_bach.size() > 0 && Chunk.buffer_wait[i] == false)
				{
					Chunk c = chunk_bach.get(0);
					chunk_bach.remove(0);
					
					c.pre_build_chunk(i);
					this.objs_drawable.add(c);
				}
				i++;
			}
			try { Thread.sleep(10); } catch (InterruptedException e) {}
			updatePFps();
		}
	}
	
}
