package com.vox.loop;

import java.sql.Time;

public class LoopMotor {
	public long		graphRate = (long)((double) 1000000000L) / 60L;
	public long		controllerRate = (long)((double) 1000000000L) / 100L;
	public boolean	running = false;
	
	private GraphicLoop graphloop;
	private ControllerLoop controllerloop;
	private MotorGraphics motor;
	
	public LoopMotor(MotorGraphics motor)
	{
		this.motor = motor;
	}
	
	public void start() {

		if (running)
			return;
		running = true;
		graphloop = new GraphicLoop();
		//controllerloop = new ControllerLoop();

		//controllerloop.start();
		
		graphloop.run();
	}

	public void stop() {
		if (!running)
			return;

		running = false;
	}
	
	private void call(int id) {
		switch (id) {
			case 1:
				motor.graphicRenderingLoop();
				break ;
			case 2:
				motor.graphicControllerLoop();
				break ;
		}
	}
	
	public class GraphicLoop implements Runnable {

		public GraphicLoop() {
		
		}
		
		@Override
		public void run() {

			long frames = 0;
			long frameCounter = 0;

			long lastTime = System.currentTimeMillis();

			while (running) {
				
				if ((System.currentTimeMillis() - lastTime) > (1000/60))
				{
					//System.out.println("HHH  -> " + (System.currentTimeMillis() - lastTime) +  " " + (1000/60));
					lastTime = System.currentTimeMillis();
					call(1);
					//System.out.println("HHH2 -> " + (System.currentTimeMillis() - lastTime) +  " " + (1000/60));
				}
				try { Thread.sleep(20); } catch (Exception e) {}
			}
		}
	}
	
	public class ControllerLoop implements Runnable {

		private Thread _t;
		
		public ControllerLoop() {
			this._t = new Thread(this);
		}
		
		public void start() {
			this._t.start();
		}
		
		@Override
		public void run() {

			long lastTime = getTime();
			
			while (running) {
				
				long startTime = getTime();
				long passedTime = startTime - lastTime;
				
				if (passedTime > controllerRate)
				{
					lastTime = getTime();
					call(2);
				} else {
					try { Thread.sleep(10); } catch (Exception e) {}
				}
			}
		}
	}
	
	public static long getTime(){
		return System.nanoTime();
	}
}
