package com.vox.utils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
//import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

	public static Map<String, Texture> list = new HashMap<String, Texture>();
	
	/**
     * Stores the handle of the texture.
     */
    public final int id;

    /**
     * Width of the texture.
     */
    private int width;
    /**
     * Height of the texture.
     */
    private int height;

    /** Creates a texture. */
    public Texture() {
        id = glGenTextures();
    }

    /**
     * Binds the texture.
     */
    public void bind2D() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
    
    /**
     * Binds the texture.
     */
    public void bind3D() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
    }

    /**
     * Sets a parameter of the texture.
     *
     * @param name  Name of the parameter
     * @param value Value to set
     */
    public void setParameter2D(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }
    
    /**
     * Sets a parameter of the texture.
     *
     * @param name  Name of the parameter
     * @param value Value to set
     */
    public void setParameter3D(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D_ARRAY, name, value);
    }

    /**
     * Uploads image data with specified width and height.
     *
     * @param width  Width of the image
     * @param height Height of the image
     * @param data   Pixel data of the image
     */
    public void uploadData2D(int width, int height, ByteBuffer data) {
        uploadData2D(GL_RGBA8, width, height, GL_RGBA, data);
    }

    /**
     * Uploads image data with specified internal format, width, height and
     * image format.
     *
     * @param internalFormat Internal format of the image data
     * @param width          Width of the image
     * @param height         Height of the image
     * @param format         Format of the image data
     * @param data           Pixel data of the image
     */
    public void uploadData2D(int internalFormat, int width, int height, int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    public void uploadData3D(int level, int internalFormat, int width, int height, int layercount, int format, ByteBuffer data) {
    	glTexImage3D(GL_TEXTURE_2D_ARRAY,
    				level,				//level
    				internalFormat,		//internalformat
    				width, height, layercount,//width height depth
    				0,					//border
    				format,				//format
    				GL_UNSIGNED_BYTE,	//type
    				data);				//data
//    	glTexSubImage3D(GL_TEXTURE_2D_ARRAY,
//    				0,					//mipmap number
//    				0, 0, 0,			//xoffset, yoffset, zoffset
//    				width, height, layercount,	//width height depth
//    				format,				//format
//    				GL_UNSIGNED_BYTE,	//type
//    				data);				//data
    }
    /**
     * Delete the texture.
     */
    public void delete() {
        glDeleteTextures(id);
    }

    /**
     * Gets the texture width.
     *
     * @return Texture width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the texture width.
     *
     * @param width The width to set
     */
    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
        }
    }

    /**
     * Gets the texture height.
     *
     * @return Texture height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the texture height.
     *
     * @param height The height to set
     */
    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
        }
    }

    /**
     * Creates a texture with specified width, height and data.
     *
     * @param width  Width of the texture
     * @param height Height of the texture
     * @param data   Picture Data in RGBA format
     *
     * @return Texture from the specified data
     */
    public static Texture createTexture2D(int width, int height, ByteBuffer data) {
        Texture texture = new Texture();
        texture.setWidth(width);
        texture.setHeight(height);

        texture.bind2D();

        texture.setParameter2D(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        texture.setParameter2D(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        //texture.setParameter2D(GL_TEXTURE_WRAP_S, GL_REPEAT);
        //texture.setParameter2D(GL_TEXTURE_WRAP_T, GL_REPEAT);
        texture.setParameter2D(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameter2D(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        texture.uploadData2D(GL_RGBA8, width, height, GL_RGBA, data);

        return texture;
    }
    
    public static Texture createTexture3D(int size, int width, int height, ByteBuffer[] data) {
        Texture texture = new Texture();
        texture.setWidth(width);
        texture.setHeight(height);
        texture.bind3D();

        texture.setParameter3D(GL_TEXTURE_WRAP_S, GL_REPEAT);
        texture.setParameter3D(GL_TEXTURE_WRAP_T, GL_REPEAT);
        texture.setParameter3D(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameter3D(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        //glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, width, height, size);
        for (int i = 0; i < size; i++)
        {
        	System.out.println("LA");
        	texture.uploadData3D(i, GL_RGBA8, width, height, size, GL_RGBA, data[i]);
        }
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, 4);
        glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
        //glBindTexture(GL_TEXTURE_2D_ARRAY, texture.id);
//        for (int i = 0; i < size; i++)
//        {
//        	org.lwjgl.opengl.GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);;
//        	glTexImage3D(GL_TEXTURE_2D_ARRAY, i, GL_RGBA8, width/(i * 2), height/(i * 2), size, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
//        }
        return texture;
    }

    /**
     * Load texture from file.
     *
     * @param path File path of the texture
     *
     * @return Texture from specified file
     */
    public static Texture loadTexture2D(String id, String path) {
        ByteBuffer image;
        int width, height;
        Texture t;
        
        if (Texture.list.containsKey(id))
        	return (Texture.list.get(id));
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(false);
            image = stbi_load(path, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"
                                           + System.lineSeparator() + stbi_failure_reason());
            }
            /* Get width and height of image */
            width = w.get();
            height = h.get();
        }
        t = createTexture2D(width, height, image);
        Texture.list.put(id, t);
        return (t);
    }
    
    public static Texture loadTexture3D(String id, int width, int height, String ... paths) {
        ByteBuffer[] images = new ByteBuffer[paths.length];
        Texture t = null;
        
        if (Texture.list.containsKey(id))
        	return (Texture.list.get(id));
        int size = 0;
        for (int i = 0; i < paths.length; i++)
        {
        	ByteBuffer image;
	        try (MemoryStack stack = MemoryStack.stackPush()) {
	            /* Prepare image buffers */
	            IntBuffer w = stack.mallocInt(1);
	            IntBuffer h = stack.mallocInt(1);
	            IntBuffer comp = stack.mallocInt(1);
	
	            /* Load image */
	            stbi_set_flip_vertically_on_load(true);
	            image = stbi_load(paths[i], w, h, comp, 4);
	            if (image == null) {
	                throw new RuntimeException("Failed to load a texture file!"
	                                           + System.lineSeparator() + stbi_failure_reason());
	            }
	            /* Get width and height of image */
	            if (w.get() != width)
	            {
	            	throw new RuntimeException("Failed to load a texture (width) " + id + " " + w.get() +" != " + width);
	            }
	            if (h.get() != width)
	            {
	            	throw new RuntimeException("Failed to load a texture (height) " + id + " " + h.get() +" != " + height);
	            }
	        }
	        images[i] = image;
	        size += image.position();
        }
        //System.out.println(Sizeof.Integer(((width * height) * paths.length)));
        //System.out.println(size);
        //System.out.println((width * paths.length) + " " + (height * paths.length));
        t = createTexture3D(paths.length, width, height, images);
        Texture.list.put(id, t);
        return (t);
    }
}
