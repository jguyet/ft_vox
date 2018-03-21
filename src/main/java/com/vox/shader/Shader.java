package com.vox.shader;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import com.vox.utils.Util;

public class Shader {

	public int id = 0;
	//public ShaderProgram program;
	
	public Shader(String vertex_path, String fragment_path)
	{
		try {
			//this.program = new ShaderProgram();
			
//			this.program.createVertexShader(read(vertex_path));
//			this.program.createFragmentShader(read(fragment_path));
//			
//			try {
//				this.program.link();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			this.id = Util.createShaderProgram(read(vertex_path), read(fragment_path));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			this.id = glCreateProgram();
//			int vshader = Shader.createShader(vertex_path, GL_VERTEX_SHADER);
//			int fshader = Shader.createShader(fragment_path, GL_FRAGMENT_SHADER);
//			glAttachShader(this.id, vshader);
//			glAttachShader(this.id, fshader);
//			glLinkProgram(this.id);
//			
//			int compiled = glGetProgrami(this.id, GL_COMPILE_STATUS);
//			String shaderLog = glGetProgramInfoLog(this.id);
//			if (shaderLog != null && shaderLog.trim().length() > 0) {
//				System.err.println(shaderLog);
//			}
//			if (compiled == 0) {
//				throw new AssertionError("Could not compile shader");
//			}
//			glDetachShader(this.id, vshader);
//			glDetachShader(this.id, fshader);
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
	}
	
	private static String read(String path) {
		try {
		FileInputStream stream = new FileInputStream(new File(path));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().reduce((acc, line) -> acc+"\n"+line).get();
		} catch (Exception e)
		{
		}
		return "";
    }
	
//	/**
//	 * Create a shader object from the given classpath resource.
//	 *
//	 * @param resource
//	 *            the class path
//	 * @param type
//	 *            the shader type
//	 *
//	 * @return the shader object id
//	 *
//	 * @throws IOException
//	 */
//	public static int createShader(String resource, int type) throws IOException {
//		return createShader(resource, type, null);
//	}
//	
//	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
//		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
//		buffer.flip();
//		newBuffer.put(buffer);
//		return newBuffer;
//	}
//	
//	/**
//	 * Reads the specified resource and returns the raw data as a ByteBuffer.
//	 *
//	 * @param resource   the resource to read
//	 * @param bufferSize the initial buffer size
//	 *
//	 * @return the resource data
//	 *
//	 * @throws IOException if an IO error occurs
//	 */
//    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
//        ByteBuffer buffer;
//        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
//        File file = null;
//        
//        if (url == null)
//        	file = new File(resource);
//        else
//        	file = new File(url.getFile());
//        if (file.isFile()) {
//            FileInputStream fis = new FileInputStream(file);
//            FileChannel fc = fis.getChannel();
//            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//            fc.close();
//            fis.close();
//        } else {
//            buffer = BufferUtils.createByteBuffer(bufferSize);
//            InputStream source = url.openStream();
//            if (source == null)
//                throw new FileNotFoundException(resource);
//            try {
//                ReadableByteChannel rbc = Channels.newChannel(source);
//                try {
//                    while (true) {
//                        int bytes = rbc.read(buffer);
//                        if (bytes == -1)
//                            break;
//                        if (buffer.remaining() == 0)
//                            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
//                    }
//                    buffer.flip();
//                } finally {
//                    rbc.close();
//                }
//            } finally {
//                source.close();
//            }
//        }
//        return buffer;
//    }
//	
//	/**
//	 * Create a shader object from the given classpath resource.
//	 *
//	 * @param resource
//	 *            the class path
//	 * @param type
//	 *            the shader type
//	 * @param version
//	 *            the GLSL version to prepend to the shader source, or null
//	 *
//	 * @return the shader object id
//	 *
//	 * @throws IOException
//	 */
//	public static int createShader(String resource, int type, String version) throws IOException {
//		int shader = glCreateShader(type);
//
//		System.out.println(resource);
//		ByteBuffer source = ioResourceToByteBuffer(resource, 8192);
//
//		if ( version == null ) {
//			PointerBuffer strings = BufferUtils.createPointerBuffer(1);
//			IntBuffer lengths = BufferUtils.createIntBuffer(1);
//
//			strings.put(0, source);
//			lengths.put(0, source.remaining());
//
//			glShaderSource(shader, strings, lengths);
//		} else {
//			PointerBuffer strings = BufferUtils.createPointerBuffer(2);
//			IntBuffer lengths = BufferUtils.createIntBuffer(2);
//
//			ByteBuffer preamble = memUTF8("#version " + version + "\n", false);
//
//			strings.put(0, preamble);
//			lengths.put(0, preamble.remaining());
//
//			strings.put(1, source);
//			lengths.put(1, source.remaining());
//
//			glShaderSource(shader, strings, lengths);
//		}
//
//		glCompileShader(shader);
//		int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
//		String shaderLog = glGetShaderInfoLog(shader);
//		if (shaderLog != null && shaderLog.trim().length() > 0) {
//			System.err.println(shaderLog);
//		}
//		if (compiled == 0) {
//			throw new AssertionError("Could not compile shader");
//		}
//		return shader;
//	}
}
