package com.vox.utils;

public class Sizeof {

	public static int Float(int length)
	{
		return (length*(Float.SIZE/Byte.SIZE));
	}
	
	public static int Short(int length)
	{
		return (length*(Short.SIZE/Byte.SIZE));
	}
	
	public static int Integer(int length)
	{
		return (length*(Integer.SIZE/Byte.SIZE));
	}
	
	public static int Double(int length)
	{
		return (length*(Double.SIZE/Byte.SIZE));
	}
}
