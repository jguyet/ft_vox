package com.vox.loop;

import java.sql.Time;

public class LoopMotor {
	public long		graphRate = (long)((double) 1000L) / 80L;
	public long		controllerRate = (long)((double) 1000L) / 80L;
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
		controllerloop = new ControllerLoop();

		controllerloop.start();
		
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
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			long lastTime = System.currentTimeMillis();

			while (running) {
				
				if ((System.currentTimeMillis() - lastTime) > graphRate)
				{
					lastTime = System.currentTimeMillis();
					call(1);
				}
				try { Thread.sleep(5); } catch (Exception e) {}
			}
		}
	}
	
	public class ControllerLoop implements Runnable {

		private Thread _t;
		
		public ControllerLoop() {
			this._t = new Thread(this);
			this._t.setPriority(Thread.MAX_PRIORITY);
			this._t.setDaemon(false);
		}
		
		public void start() {
			this._t.start();
		}
		
		@Override
		public void run() {
			long lastTime = System.currentTimeMillis();

			while (running) {
				
				if ((System.currentTimeMillis() - lastTime) > controllerRate)
				{
					lastTime = System.currentTimeMillis();
					call(2);
				}
				try { Thread.sleep(5); } catch (Exception e) {}
			}
		}
	}
	
	public static long getTime(){
		return System.nanoTime();
	}
}
