package com.vox.graphics;

public class Screen {

	public int width;
	public int height;
	public int middleWidth;
	public int middleHeight;
	
	public Screen(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.middleWidth = width / 2;
		this.middleHeight = height / 2;
	}
}
