package com.vox.utils;

import java.util.Random;

import org.joml.Vector2f;

import com.flowpowered.noise.module.Module;
import com.flowpowered.noise.module.modifier.Clamp;
import com.flowpowered.noise.module.modifier.ScaleBias;
import com.flowpowered.noise.module.modifier.ScalePoint;
import com.flowpowered.noise.module.source.Perlin;

public class Noise {
	private long seed;
	private Random rand;
	private int octave;
	private float amplitude;
	
	public Noise(long seed, int octave, float amplitude) {
		this.seed = seed;
		this.octave = octave;
		this.amplitude = amplitude;
		
		rand = new Random();
	}
	
	public float getNoise(double x, double z, double height, int inverse) {
		int xmin = (int) (double) x / octave;
		int xmax = (int) xmin + 1;
		int zmin = (int) (double) z / octave;
		int zmax = (int) zmin + 1;
		
		Vector2f a = new Vector2f(xmin, zmin);
		Vector2f b = new Vector2f(xmax, zmin);
		Vector2f c = new Vector2f(xmax, zmax);
		Vector2f d = new Vector2f(xmin, zmax);
		
		float ra = (float) noise(a);
		float rb = (float) noise(b);
		float rc = (float) noise(c);
		float rd = (float) noise(d);
		
		float t1 = (((float)x) - xmin * octave) / octave;
		float t2 = (((float)z) - zmin * octave) / octave;
		
		float i1 = interpolate(ra, rb, t1);
		float i2 = interpolate(rd, rc, t1);
		float h  = interpolate(i1, i2, t2);
		
		return ((h * 2 - 1) * inverse + ((float)height)) * amplitude; 
	}
	
	private float interpolate(float a, float b, float t) {
		float ft = (float) (t * Math.PI);
		float f = (float) ((1f - Math.cos(ft)) * 0.5f);
		
		return a * (1f - f) + b * f;
	}
	
	private double noise(Vector2f coord) {
		rand.setSeed((long) (coord.x * 43594546 + coord.y * 11438876 + seed));
		return rand.nextDouble();
	}

	public long getSeed() {
		return seed;
	}

	public int getOctave() {
		return octave;
	}

	public float getAmplitude() {
		return amplitude;
	}
}
