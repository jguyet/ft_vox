package com.vox.utils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;

public class Util {
	public static int createShaderProgram(String vertexSource, String fragmentSource) throws Exception {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertexShader, vertexSource);
        glShaderSource(fragmentShader, fragmentSource);

        glCompileShader(vertexShader);
        glCompileShader(fragmentShader);

        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new Exception(glGetShaderInfoLog(vertexShader));
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new Exception(glGetShaderInfoLog(fragmentShader));

        int shaderProgram = glCreateProgram();

        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE)
            throw new Exception(glGetProgramInfoLog(shaderProgram));

        return shaderProgram;
    }
	
	public static float[] add_to_float_tab(float[] first, float[] second)
	{
		float[] res = new float[first.length + second.length];
		ByteBuffer b = BufferUtils.createByteBuffer(Sizeof.Float(first.length + second.length));
		FloatBuffer vb = b.asFloatBuffer();
		
		vb.put(first);
		vb.put(second);
		vb.flip();
		vb.get(res);
		return (res);
	}
	
	public static int getRandomValue(int i1, int i2)
	{
		if (i2 < i1)
			return 0;
		Random rand = new Random();
		return (rand.nextInt((i2 - i1) + 1)) + i1;
	}
	
	public static float randFloat(float min, float max) {

	    Random rand = new Random();

	    float result = rand.nextFloat() * (max - min) + min;

	    return result;

	}
	
	public static String read(String path) throws RuntimeException {
		String content = "";
		try {
			if (new File(path).exists() == false)
				throw new FileNotFoundException("Failed to load a File " + path);
			FileInputStream stream = new FileInputStream(new File(path));
	        @SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	        content = reader.lines().reduce((acc, line) -> acc+"\n"+line).get();
		} catch (Exception e)
		{
			throw new RuntimeException("Failed to load a File " + path);
		}
		return content;
    }
}
