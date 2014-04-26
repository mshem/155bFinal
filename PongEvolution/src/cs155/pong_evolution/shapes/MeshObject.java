package cs155.pong_evolution.shapes;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Scanner;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import cs155.opengl.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * this reads in an objfile from the InputStream
 * 
 * @author tim 
 * 
 */

public class MeshObject {
	/*
	 * read the vertices, normals, and texturecoordinates from the objfile
	 * create the buffers of vertices, normals, and textcoords using the face
	 * indices where each vertex of each face has a unique index ...
	 */

	/*
	 * these arrays store the vertices and normals as specified in the obj file
	 */
	private ArrayList<Float> vertex = new ArrayList<Float>();
	private ArrayList<Float> normal = new ArrayList<Float>();
	private ArrayList<float[]> vertexList = new ArrayList<float[]>();
	private ArrayList<float[]> textureList = new ArrayList<float[]>();
	private ArrayList<float[]> normalList = new ArrayList<float[]>();
	/*
	 * we actually don't need the face array list as the newface list will
	 * simply be a sequential list of indices
	 */
	private ArrayList<Short> face = new ArrayList<Short>();

	private ArrayList<float[]> newvertex = new ArrayList<float[]>();
	private ArrayList<float[]> newnormal = new ArrayList<float[]>();
	private ArrayList<float[]> newtexture = new ArrayList<float[]>();
	private ArrayList<Short> newface = new ArrayList<Short>();

	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
	private FloatBuffer mNormalBuffer;
	private ShortBuffer mFaceIndexBuffer; // used when polygon is filled

	private java.io.InputStream input;
	
	private int[] textures = new int[3];
	private int filter=2; // allow us to switch the texture filter

	public MeshObject(java.io.InputStream input) {
		this.input = input;

		readData(); // load the face and vertex data into ArrayLists
		toBuffers(); // convert the vertices and faces to buffers for use by the
						// OpenGL drawElements method

	}
	
	public MeshObject(Context context, String filename) throws IOException{
		this(context.getAssets().open( filename ));		
	}
	

	/**
	 * this converts the arraylists of vertices, texturecoords, and normals into
	 * buffers as needed by drawElement
	 */
	public void toBuffers() {

		//System.println("creating buffers");

		float[] vertices = new float[newvertex.size()*3 ];
		int i = 0;

		System.out.println("creating vertex buffer");
		for (float[] p : newvertex) {
			System.out.println("v "+i+" "+p[0]+" "+p[1]+" "+p[2]+" ");
			vertices[i++] = p[0];
			vertices[i++] = p[1];
			vertices[i++] = p[2];

		}

		mVertexBuffer = getNewFloatBuffer(vertices);
		
		float[] texturecoords = new float[newvertex.size()*2];
		i = 0;

		//System.out.println("creating vertex buffer");
		for (float[] p : newtexture) {
			System.out.println("t "+i+" "+p[0]+" "+p[1]);
			texturecoords[i++] = p[0];
			texturecoords[i++] = p[1];
		}

		mTextureBuffer = getNewFloatBuffer(texturecoords);

		float[] normals = new float[newvertex.size()* 3];
		i = 0;

		//System.out.println("creating normal buffer");
		for (float[] p : newnormal) {
			System.out.println("n "+i+" "+p[0]+" "+p[1]+" "+p[2]+" ");
			normals[i++] = p[0];
			normals[i++] = p[1];
			normals[i++] = p[2];
		}

		mNormalBuffer = getNewFloatBuffer(normals);

		//System.out.println("creating face buffer");
		short[] faces = new short[newface.size()];
		i = 0;
		for (Short t : newface) {
			System.out.println("f "+i+" "+(i%3)+" "+t);
			faces[i++] = t;
		}
		mFaceIndexBuffer = getNewShortBuffer(faces);
		System.out.println("created buffers");
		return;
	}

	public void oldToBuffers() {

		float[] vertices = new float[vertex.size() * 3];
		int i = 0;

		//System.out.println("creating vertex buffer");
		for (Float p : vertex) {
			vertices[i++] = p;
		}

		mVertexBuffer = getNewFloatBuffer(vertices);

		float[] normals = new float[vertex.size() * 3];
		i = 0;

		//System.out.println("creating normal buffer");
		for (Float p : normal) {
			normals[i++] = p;
		}

		mNormalBuffer = getNewFloatBuffer(normals);

		//System.out.println("creating face buffer");
		short[] faces = new short[face.size() * 3];
		i = 0;
		for (Short t : face) {
			faces[i++] = t;
		}
		mFaceIndexBuffer = getNewShortBuffer(faces);
		//System.out.println("created buffers");
		return;
	}

	/**
	 * convert an array of floats into a FloatBuffer
	 * 
	 * @param vertices
	 * @return
	 */
	public static FloatBuffer getNewFloatBuffer(float[] vertices) {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(vertices);
		buffer.position(0);
		return buffer;
	}

	/**
	 * convert an array of shorts into a ShortBuffer
	 * 
	 * @param v
	 * @return
	 */
	public static ShortBuffer getNewShortBuffer(short[] v) {
		ByteBuffer bb = ByteBuffer.allocateDirect(v.length * 2);
		bb.order(ByteOrder.nativeOrder()); // Use native byte order
		ShortBuffer buffer = bb.asShortBuffer();
		buffer.put(v); // Copy data into buffer
		buffer.position(0); // Rewind
		return buffer;
	}

	/**
	 * this draws the mesh object as a sequence of textured triangles
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		//Bind the texture according to the set texture filter
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[filter]);

		gl.glFrontFace(GL10.GL_CCW);
		gl.glColor4f(0.0f,0.0f,1.0f,1.0f); //blue
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 3, mNormalBuffer);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		gl.glDrawElements(GL10.GL_TRIANGLES, newface.size(),
				GL10.GL_UNSIGNED_SHORT, mFaceIndexBuffer);

		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	public void readData() {
		// Group3D g = new Group3D();
		Scanner scanner = null;
		// open file
		// read all data into ArrayLists for
		// vertices, normals, texture coords, and faces
		// add faces to a Group3D object and
		try {
			scanner = new Scanner(this.input);

			scanner.useDelimiter("\\n");
			while (scanner.hasNext()) {
				String s = scanner.next();
				//System.out.println("processing " + s);
				Scanner line = new Scanner(s);
				if (!line.hasNext())
					continue;
				String firstToken = line.next();
				if (firstToken.equals("v")) {
					processVertex(line);
				} else if (firstToken.equals("vt")) {
					processTextureCoord(line);
				} else if (firstToken.equals("vn")) {
					processVertexNormal(line);
				} else if (firstToken.equals("f")) {
					processFace(line);
				}
				line.close();

			}

		} catch (Exception e) {
			System.out.println("***** ERROR in ObjReader ****");
			e.printStackTrace();
		}

		// return g;
	}

	private void processVertex(Scanner s) {
		float d1 = s.nextFloat();
		float d2 = s.nextFloat();
		float d3 = s.nextFloat();
		vertex.add(d1);
		vertex.add(d2);
		vertex.add(d3);
		vertexList.add(new float[] { d1, d2, d3 });
	}

	private void processTextureCoord(Scanner s) {
		float d1 = s.nextFloat();
		float d2 = s.nextFloat();
		this.textureList.add(new float[] { d1, d2 });

		// TextureCoordinate tc = new TextureCoordinate(d1, d2);
		// this.textcoord.add(tc);
		// System.out.println("tc "+tc);
	}

	private void processVertexNormal(Scanner s) {
		float d1 = s.nextFloat();
		float d2 = s.nextFloat();
		float d3 = s.nextFloat();
		normal.add(d1);
		normal.add(d2);
		normal.add(d3);
		this.normalList.add(new float[] { d1, d2, d3 });
		// Point3D p = new Point3D(d1, d2, d3);
		// this.normal.add(p);
		// System.out.println("n "+p);
	}

	private static int[] readVertexData(String s) {
		int[] data = new int[3];
		int n1 = s.indexOf("/");
		int n2 = s.indexOf("//");
		if (n1 == -1) {
			data[0] = Integer.parseInt(s);
			data[1] = data[2] = 0;
		} else if (n2 == -1) {
			int n3 = s.indexOf('/', n1 + 1);
			data[0] = Integer.parseInt(s.substring(0, n1));
			if (n3 == -1) {
				data[1] = Integer.parseInt(s.substring(n1 + 1));
				data[2] = 0;
			} else {
				data[1] = Integer.parseInt(s.substring(n1 + 1, n3));
				data[2] = Integer.parseInt(s.substring(n3 + 1));
			}
		} else {
			data[0] = Integer.parseInt(s.substring(0, n2));
			data[1] = 0;
			data[2] = Integer.parseInt(s.substring(n2 + 2));
		}

		return data;
	}

	private void processFace(Scanner s) {
		/*
		 * we need to read the 1,2, or 3 integer indices and store them in an
		 * array of 3 ints....
		 */
		int[] d0, d1, d2, d3;
		String v0 = s.next();
		d0 = readVertexData(v0);
		String v1 = s.next();
		d1 = readVertexData(v1);
		String v2 = s.next();
		d2 = readVertexData(v2);
		// d3 = new int[3];

		addVertexTextureNormalFace(d0);
		addVertexTextureNormalFace(d1);
		addVertexTextureNormalFace(d2);
		
		if (d0[1] == 0) {
			float[] tca={0f,0f}, tcb={1f,0f},tcc={0f,1f};
			this.newtexture.add(tca);
			this.newtexture.add(tcb);
			this.newtexture.add(tcc);			
		}

		if (d0[2] == 0) {
			// create normal vectors for the face
			float[] a = this.vertexList.get(d0[0]-1);
			float[] b = this.vertexList.get(d1[0]-1);
			float[] c = this.vertexList.get(d2[0]-1);
			float[] n = findNormal(a, b, c);
			this.newnormal.add(n);
			this.newnormal.add(n);
			this.newnormal.add(n);
		}

		// this.face.add((short)(d0[0]-1));
		// this.face.add((short)(d1[0]-1));
		// this.face.add((short)(d2[0]-1));

		// if the face is a quad, then draw it as two triangles
		if (s.hasNext()) {
			String v3 = s.next();
			d3 = readVertexData(v3);
			addVertexTextureNormalFace(d0);
			addVertexTextureNormalFace(d2);
			addVertexTextureNormalFace(d3);
			
			if (d0[1] == 0) {
				float[] tca={0f,0f}, tcb={1f,0f},tcc={0f,1f};
				this.newtexture.add(tca);
				this.newtexture.add(tcb);
				this.newtexture.add(tcc);			
			}

			if (d0[2] == 0) {
				// create normal vectors for the face
				float[] a = this.vertexList.get(d0[0]-1);
				float[] b = this.vertexList.get(d2[0]-1);
				float[] c = this.vertexList.get(d3[0]-1);
				float[] n = findNormal(a, b, c);
				this.newnormal.add(n);
				this.newnormal.add(n);
				this.newnormal.add(n);
			}

			// this.face.add((short)(d0[0]-1));
			// this.face.add((short)(d2[0]-1));
			// this.face.add((short)(d3[0]-1));
		}
	}

	/*
	 * we return the cross product of b-a and c-a to get the outward facing
	 * normal assuming a CCW orientation of vertices when viewed from the
	 * outside
	 */
	private float[] findNormal(float[] a, float[] b, float[] c) {
		float ux = b[0] - a[0], uy = b[1] - a[1], uz = b[2] - a[2], vx = c[0]
				- a[0], vy = c[1] - a[1], vz = c[2] - a[2];
		float[] n = { uy * vz - uz * vy, uz * vx - ux * vz, ux * vy - uy * vx };
		return n;

	}

	private short newvertexnum = 0;

	/*
	 * given the indices of the vertex and normal for the ith face vertex
	 */
	private void addVertexTextureNormalFace(int[] d) {
		System.out.println("addVTN "+newvertexnum+" "+d[0]+" "+d[1]+" "+d[2]+
		  " "+this.newface.size()+" "+this.newvertex.size()+" "+
				this.newnormal.size()+" "+this.newtexture.size());
		this.newface.add(newvertexnum++);
		this.newvertex.add(this.vertexList.get(d[0]-1));
		if (d[1] != 0) {
			this.newtexture.add(this.textureList.get(d[1]-1));
		}
		if (d[2] != 0) {
			this.newnormal.add(this.normalList.get(d[2]-1));
		}
	}
	
	
	/**
	 * Load the textures
	 * 
	 * @param gl - The GL Context
	 * @param context - The Activity context
	 */
	
	public void loadGLTexture(GL10 gl, Context context) {
		loadGLTexture(gl,context,R.drawable.crate);
		
	}
	public void loadGLTexture(GL10 gl, Context context, int imageid) {
		//Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(imageid);
		Bitmap bitmap = null;
		try {
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);

		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}

		//Generate there texture pointer
		gl.glGenTextures(3, textures, 0);

		//Create Nearest Filtered Texture and bind it to texture 0
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create Linear Filtered Texture and bind it to texture 1
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create mipmapped textures and bind it to texture 2
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
		/*
		 * This is a change to the original tutorial, as buildMipMap does not exist anymore
		 * in the Android SDK.
		 * 
		 * We check if the GL context is version 1.1 and generate MipMaps by flag.
		 * Otherwise we call our own buildMipMap implementation
		 */
		if(gl instanceof GL11) {
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			
		//
		} else {
			buildMipmap(gl, bitmap);
		}		
		
		//Clean up
		bitmap.recycle();
	}
	
	/**
	 * Our own MipMap generation implementation.
	 * Scale the original bitmap down, always by factor two,
	 * and set it as new mipmap level.
	 * 
	 * Thanks to Mike Miller (with minor changes)!
	 * 
	 * @param gl - The GL Context
	 * @param bitmap - The bitmap to mipmap
	 */
	private void buildMipmap(GL10 gl, Bitmap bitmap) {
		//
		int level = 0;
		//
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		//
		while(height >= 1 || width >= 1) {
			//First of all, generate the texture from our bitmap and set it to the according level
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);
			
			//
			if(height == 1 || width == 1) {
				break;
			}

			//Increase the mipmap level
			level++;

			//
			height /= 2;
			width /= 2;
			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			
			//Clean up
			bitmap.recycle();
			bitmap = bitmap2;
		}
	}

}
