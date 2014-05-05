package cs155.pong_evolution.shapes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import cs155.opengl.R;
//import cs155.opengl.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * This class is an object representation of a Sphere containing the vertex
 * information, texture coordinates, the vertex indices and drawing
 * functionality, which is called by the renderer. currently not working :(
 * 
 * @author Georg Konwisser, gekonwi@brandeis.edu
 * 
 */
public class Sphere {

	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	/** The buffer holding the texture coordinates */
	private FloatBuffer textureBuffer;
	/** The buffer holding the indices */
	private ByteBuffer indexBuffer;
	/** The buffer holding the normals */
	private FloatBuffer normalBuffer;

	/** Our texture pointer */
	private int[] textures = new int[3];
	private int vertexCount;
	
	private float normals[];

	public Sphere(float radius, int stacks, int slices) {
		vertexCount = stacks * slices;
		
		initBuffers();

		fillNormalBuffer(stacks, slices);
		fillVertexBuffer(radius);
		fillIndexBuffer(stacks, slices);
		fillTextureBuffer(stacks, slices);
	}

	private void fillIndexBuffer(int stacks, int slices) {
		for (int stackNumber = 0; stackNumber < stacks; stackNumber++) {
			for (int sliceNumber = 0; sliceNumber < slices; sliceNumber++) {
//				int second = (sliceNumber * (stacks + 1)) + stackNumber;
//				int first = second + stacks + 1;

				int first = (stackNumber * slices) + (sliceNumber % slices);
				int second = ((stackNumber + 1) * slices)
						+ (sliceNumber % slices);
				
				indexBuffer.put((byte) first);
				indexBuffer.put((byte) second);
				indexBuffer.put((byte) (first + 1));

				indexBuffer.put((byte) second);
				indexBuffer.put((byte) (second + 1));
				indexBuffer.put((byte) (first + 1));
			}
		}
		
		indexBuffer.rewind();
	}

	private void fillTextureBuffer(int stacks, int slices) {
		for (int stackNumber = 0; stackNumber < stacks; stackNumber++) {
			for (int sliceNumber = 0; sliceNumber < slices; sliceNumber++) {
				float u = 1.f - ((float) sliceNumber / (float) slices);
				float v = (float) stackNumber / (float) stacks;

				textureBuffer.put(u);
				textureBuffer.put(v);
			}
		}
		
		textureBuffer.rewind();
	}

	private void fillVertexBuffer(float radius) {
		for (float normalCoord : normals)
			vertexBuffer.put(normalCoord * radius);			
		
		vertexBuffer.rewind();
	}

	private void fillNormalBuffer(int stacks, int slices) {
		normals = new float[vertexCount * 3];
		int normalIndex = -1;
		
		for (int stackNumber = 0; stackNumber < stacks; stackNumber++) {
			double theta = stackNumber * Math.PI / stacks;
			double sinTheta = Math.sin(theta);
			double cosTheta = Math.cos(theta);

			for (int sliceNumber = 0; sliceNumber < slices; sliceNumber++) {				
				double phi = sliceNumber * 2 * Math.PI / slices;
				double sinPhi = Math.sin(phi);
				double cosPhi = Math.cos(phi);

				double nx = cosPhi * sinTheta;
				double ny = sinPhi * sinTheta;
				double nz = cosTheta;

				normalIndex++;
				normals[normalIndex * 3 + 0] = (float) nx;
				normals[normalIndex * 3 + 1] = (float) ny;
				normals[normalIndex * 3 + 2] = (float) nz;
			}
		}
		
		normalBuffer.put(normals);
		normalBuffer.rewind();
	}

	private void initBuffers() {
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertexCount * 3 * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.position(0);

		//
		byteBuf = ByteBuffer.allocateDirect(vertexCount * 3 * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		normalBuffer = byteBuf.asFloatBuffer();
		normalBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(vertexCount * 2 * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.position(0);

		//
		indexBuffer = ByteBuffer.allocateDirect(vertexCount * 6);
		indexBuffer.position(0);
	}

	/**
	 * The object own drawing function. Called from the renderer to redraw this
	 * instance with possible changes in values.
	 * 
	 * @param gl
	 *            - The GL Context
	 * @param filter
	 *            - Which texture filter to be used
	 */
	public void draw(GL10 gl, int filter) {
		// Bind the texture according to the set texture filter
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[filter]);

		// Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CCW);

		// Point to our buffers
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

		// Draw the vertices as triangles, based on the Index Buffer information
		gl.glDrawElements(GL10.GL_TRIANGLES, vertexCount,
				GL10.GL_UNSIGNED_BYTE, indexBuffer);

		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}

	public void loadGLTexture(GL10 gl, Context context, int imageid) {
		// Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(imageid);
		Bitmap bitmap = null;
		try {
			// BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);

		} finally {
			// Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}

		// Generate there texture pointer
		gl.glGenTextures(3, textures, 0);

		// Create Nearest Filtered Texture and bind it to texture 0
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// Create Linear Filtered Texture and bind it to texture 1
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// Create mipmapped textures and bind it to texture 2
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR_MIPMAP_NEAREST);
		/*
		 * This is a change to the original tutorial, as buildMipMap does not
		 * exist anymore in the Android SDK.
		 * 
		 * We check if the GL context is version 1.1 and generate MipMaps by
		 * flag. Otherwise we call our own buildMipMap implementation
		 */
		if (gl instanceof GL11) {
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP,
					GL11.GL_TRUE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

			//
		} else {
			buildMipmap(gl, bitmap);
		}

		// Clean up
		bitmap.recycle();
	}

	/**
	 * Our own MipMap generation implementation. Scale the original bitmap down,
	 * always by factor two, and set it as new mipmap level.
	 * 
	 * Thanks to Mike Miller (with minor changes)!
	 * 
	 * @param gl
	 *            - The GL Context
	 * @param bitmap
	 *            - The bitmap to mipmap
	 */
	private void buildMipmap(GL10 gl, Bitmap bitmap) {
		//
		int level = 0;
		//
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		//
		while (height >= 1 || width >= 1) {
			// First of all, generate the texture from our bitmap and set it to
			// the according level
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);

			//
			if (height == 1 || width == 1) {
				break;
			}

			// Increase the mipmap level
			level++;

			//
			height /= 2;
			width /= 2;
			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height,
					true);

			// Clean up
			bitmap.recycle();
			bitmap = bitmap2;
		}
	}
}
