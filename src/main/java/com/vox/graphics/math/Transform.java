package com.vox.graphics.math;

import org.joml.Vector3f;

public class Transform {
	public Vector3f position;
	public Vector3f rotation;
	public Vector3f scale;

	public Transform() {
		this.position = new Vector3f(0,0,0);
		this.rotation = new Vector3f(0,0,0);
		this.scale = new Vector3f(0,0,0);
	}
}
