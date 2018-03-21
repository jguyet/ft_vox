package com.vox.graphics;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import com.vox.graphics.math.Mathf;
import com.vox.graphics.math.Transform;

public class Camera {
	
	public FloatBuffer	projectionMatrix;
	public FloatBuffer	viewMatrix;
	public Transform	transform;
	
	private Matrix4f	view;
	
	private float		fov;
	private float		width;
	private float		height;
	private float		zNear;
	private float		zFar;
	
	public Camera(Vector3f position, Vector3f rotation)
	{
		initialize();
		this.transform.position = position;
		this.transform.rotation = rotation;
	}
	
	private void initialize()
	{
		this.projectionMatrix = BufferUtils.createFloatBuffer(16);
		this.viewMatrix = BufferUtils.createFloatBuffer(16);
		this.view = new Matrix4f();
		this.transform = new Transform();
	}
	
	public void buildFPSProjection()
	{
        new Matrix4f()
            .perspective(Mathf.toRadians(fov), width / height, zNear, zFar)
            .get(this.projectionMatrix);
		this.updateView();
	}
	
	public void setProjection(float fov, float width, float height, float zNear, float zFar)
	{
		this.fov = fov;
		this.width = width;
		this.height = height;
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	public void move(final Vector3f direction)
	{
		float			speed = 0.09f;
		Matrix4f		mat = this.view;
		Vector3f		forward = new Vector3f(mat.m02, mat.m12, mat.m22);
		Vector3f		strafe = new Vector3f(mat.m00, mat.m10, mat.m20);
		
		this.transform.position.add((forward.mul(-direction.z).add(strafe.mul(direction.x))).mul(speed));
	}
	
	public void mouseMove(Vector2f lastMousePosition, Vector2f mousePosition)
	{
		Vector2f mouse_delta = new Vector2f(mousePosition.x, mousePosition.y).sub(lastMousePosition);

		final float mouseX_Sensitivity = 0.0025f;
		final float mouseY_Sensitivity = 0.0025f;

		if (mouse_delta.x != 0) {
			this.transform.rotation.y += (360/(Mathf.PI * 2)) * (mouseX_Sensitivity * mouse_delta.x);
			this.transform.rotation.y = this.transform.rotation.y % 360.f;
			if (this.transform.rotation.y < 0)
				this.transform.rotation.y = 360 + this.transform.rotation.y;
		}
		if (mouse_delta.y != 0) {
			this.transform.rotation.x += (360/(Mathf.PI * 2)) * (mouseY_Sensitivity * mouse_delta.y);
			this.transform.rotation.x = this.transform.rotation.x % 360.f;
			if (this.transform.rotation.x < 0)
				this.transform.rotation.x = 360 + this.transform.rotation.x;
		}
	}
	
	public void updateView()
	{
		//roll, pitch and yaw are used to store our angles in our class
		float x = this.transform.rotation.x;
		float y = this.transform.rotation.y;
		float z = this.transform.rotation.z;
		x = x * (Mathf.PI * 2) / 360;
		y = y * (Mathf.PI * 2) / 360;
		z = z * (Mathf.PI * 2) / 360;
		
		Matrix4f matRoll = new Matrix4f();
		Matrix4f matPitch = new Matrix4f();
		Matrix4f matYaw = new Matrix4f();
		
		matPitch = matPitch.rotation(x, new Vector3f(1.0f, 0.0f, 0.0f));
		matYaw = matYaw.rotation(y, new Vector3f(0.0f, 1.0f, 0.0f));
		matRoll = matRoll.rotation(z, new Vector3f(0.0f, 0.0f, 1.0f));

		Matrix4f rotate = matPitch.mul(matYaw).mul(matRoll);
		
		Matrix4f translate = new Matrix4f();
		translate = translate.translate(new Vector3f(this.transform.position).negate());
		this.view = rotate.mul(translate);
		this.view.get(this.viewMatrix);
	}
}
