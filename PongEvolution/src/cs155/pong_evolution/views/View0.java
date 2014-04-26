package cs155.pong_evolution.views;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cs155.opengl.R;
import cs155.pong_evolution.model.BallModel;
import cs155.pong_evolution.model.GameModel;
import cs155.pong_evolution.shapes.Cube;
import cs155.pong_evolution.shapes.Plane;
import cs155.pong_evolution.shapes.Square3D;
import cs155.pong_evolution.shapes.TrianglePrism;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

/**
 * This is a modification of the "Lesson 07: Texture Mapping" NeHe tutorial for
 * the Google Android Platform orginally ported to Android by Savas Ziplies
 * (nea/INsanityDesign)
 * 
 * This file contains the View and Controller components of the game. The Model
 * is in the GameModel07 class
 * 
 * @author Tim Hickey
 */
public class View0 extends GLSurfaceView implements Renderer {

	// --------- shapes -----------
	private Cube cube; // for the walls
	private Plane floorPlane; // for the floor
	private Plane wallNPlane, wallSPlane, wallEPlane, wallWPlane;
	private TrianglePrism tprism;
	private Square3D square;
	// -----------------------------

	private float width, height;

	private int filter = 1; // Which texture filter? ( NEW )

	/*
	 * The initial light values for ambient and diffuse as well as the light
	 * position ( NEW )
	 */
	private float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
	private float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightPosition = { 50.0f, 25.0f, 50.0f, 1.0f };
	private float[] lightPosition1 = { 50.0f, 10.0f, 50.0f, 1.0f };
	private float[] lightPosition2 = { 50.0f, 5.0f, 50.0f, 1.0f };

	/* The buffers for our light values ( NEW ) */
	private FloatBuffer lightAmbientBuffer;
	private FloatBuffer lightDiffuseBuffer;
	private FloatBuffer lightPositionBuffer;
	private FloatBuffer lightPositionBuffer1;
	private FloatBuffer lightPositionBuffer2;

	/*
	 * These variables store the previous X and Y values as well as a fix touch
	 * scale factor. These are necessary for the rotation transformation added
	 * to this lesson, based on the screen touches. ( NEW )
	 */
	private float oldX;
	private float oldY;

	private GameModel game;
	/** The Activity Context */
	private Context context;

	/**
	 * Instance the Cube object and set the Activity Context handed over.
	 * Initiate the light buffers and set this class as renderer for this now
	 * GLSurfaceView. Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen and Buttons
	 * 
	 * @param context
	 *            - The Activity Context
	 */
	public View0(Context context) {
		super(context);
		game = new GameModel();

		// Set this as Renderer
		this.setRenderer(this);

		// Request focus, otherwise buttons won't react
		this.requestFocus();
		this.setFocusableInTouchMode(true);

		//
		this.context = context;

		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = byteBuf.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = byteBuf.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer1 = byteBuf.asFloatBuffer();
		lightPositionBuffer1.put(lightPosition1);
		lightPositionBuffer1.position(0);

		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer2 = byteBuf.asFloatBuffer();
		lightPositionBuffer2.put(lightPosition2);
		lightPositionBuffer2.position(0);

		//
		cube = new Cube();
		floorPlane = new Plane();
		wallNPlane = new Plane();
		wallSPlane = new Plane();
		wallEPlane = new Plane();
		wallWPlane = new Plane();
		tprism = new TrianglePrism();
		square = new Square3D();

		/*
		 * try { suzanne = new MeshObject(context,"obj/suzanne2.obj"); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace();
		 * System.out.println("no such file 'obj/cubesds.obj' in assets"); }
		 */

	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// And there'll be light!
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer); // Setup
																			// The
																			// Ambient
																			// Light
																			// (
																			// NEW
																			// )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer); // Setup
																			// The
																			// Diffuse
																			// Light
																			// (
																			// NEW
																			// )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer); // Position
																				// The
																				// Light
																				// (
																				// NEW
																				// )
		gl.glEnable(GL10.GL_LIGHT0); // Enable Light 0 ( NEW )

		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientBuffer); // Setup
																			// The
																			// Ambient
																			// Light
																			// (
																			// NEW
																			// )
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuseBuffer); // Setup
																			// The
																			// Diffuse
																			// Light
																			// (
																			// NEW
																			// )
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPositionBuffer1); // Position
																				// The
																				// Light
																				// (
																				// NEW
																				// )
		gl.glEnable(GL10.GL_LIGHT1); // Enable Light 0 ( NEW )

		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, lightAmbientBuffer); // Setup
																			// The
																			// Ambient
																			// Light
																			// (
																			// NEW
																			// )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, lightDiffuseBuffer); // Setup
																			// The
																			// Diffuse
																			// Light
																			// (
																			// NEW
																			// )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, lightPositionBuffer2); // Position
																				// The
																				// Light
																				// (
																				// NEW
																				// )
		gl.glEnable(GL10.GL_LIGHT2); // Enable Light 0 ( NEW )

		// Settings
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_DITHER); // Disable dithering ( NEW )
		gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0f, 0f, 1.0f, 1f); // White Background
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// Load the texture for the cube once during Surface creation
		cube.loadGLTexture(gl, this.context, R.drawable.crate);
		floorPlane.loadGLTexture(gl, this.context, R.drawable.tiles);
		// suzanne.loadGLTexture(gl, context,R.drawable.bg);
		wallNPlane.loadGLTexture(gl, context, R.drawable.crate);
		wallEPlane.loadGLTexture(gl, context, R.drawable.crate);
		wallSPlane.loadGLTexture(gl, context, R.drawable.crate);
		wallWPlane.loadGLTexture(gl, context, R.drawable.crate);
		tprism.loadGLTexture(gl, context, R.drawable.icon);
	}

	/**
	 * If the surface changes, reset the viewport
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) {
			height = 1;
		} // Prevent A Divide By Zero By
			// Making Height Equal One

		this.width = width; // store width/height for use by other view methods
		this.height = height;

		gl.glViewport(0, 0, width, height); // Reset The Current Viewport

		setViewFromAbove(gl);

		gl.glLoadIdentity(); // Reset The Modelview Matrix
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		game.update();
		
		// Clear Screen And Depth Buffer
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		setViewFromAbove(gl);

		drawFloor(gl);
		drawBall(gl);
	}

	private void drawBall(GL10 gl) {
		gl.glPushMatrix();
		
		gl.glTranslatef(game.getBall().pos[0], game.getBall().pos[1],
				game.getBall().pos[2]);
		gl.glScalef(10f, 10f, 10f);
		gl.glColor4f(1f, 1f, 1f, 1f); // draw ball in black
		
		square.draw(gl, filter);
		
		gl.glPopMatrix();
	}

	private void drawWalls(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(game.width / 2, 0f, game.height / 2);

		gl.glPushMatrix();
		drawWall(gl, wallSPlane);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(90, 0f, 1f, 0f);
		drawWall(gl, wallWPlane);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(180, 0f, 1f, 0f);
		drawWall(gl, wallNPlane);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(270, 0f, 1f, 0f);
		drawWall(gl, wallEPlane);
		gl.glPopMatrix();

		gl.glPopMatrix();
	}

	private void drawWall(GL10 gl, Plane wall) {
		gl.glPushMatrix();
		gl.glTranslatef(-game.width / 2f, 0f, -game.height / 2);
		gl.glRotatef(-90, 1f, 0f, 0f);
		gl.glScalef(game.width, 1f, game.height / 10);
		/*
		 * gl.glMatrixMode(GL10.GL_TEXTURE); gl.glPushMatrix();
		 * gl.glRotatef(90f,0f,0f,1f); gl.glPopMatrix();
		 * gl.glMatrixMode(GL10.GL_MODELVIEW);
		 */
		wall.draw(gl, filter);
		gl.glPopMatrix();
	}

	private void setViewFromAbove(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Set the properties of the camera
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		// Point and aim the camera
		float[] eye = { game.width / 2f, 300f, game.height / 2f};
		float[] center = { game.width / 2f, 0f, game.height / 2f };
		float[] up = { 0f, 0f, -1f };

		GLU.gluLookAt(gl, eye[0], eye[1], eye[2], 
				center[0], center[1], center[2], 
				up[0], up[1], up[2]);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
	}

	private double calcHeading(BallModel b) {
		float x = b.vel[0];
		float z = b.vel[2];
		double heading = Math.atan2(x, z) / Math.PI * 180;
		return heading;
	}

	private void drawFloor(GL10 gl) {
		gl.glPushMatrix();
		gl.glScalef(game.width, 1f, game.height);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glScalef(8f, 1f, 0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		floorPlane.draw(gl, filter);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
	}

	long lastEvent = System.currentTimeMillis();

	/* ***** Listener Events ( NEW ) ***** */
	/**
	 * Override the touch screen listener.
	 * 
	 * React to moves and presses on the touchscreen.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//
		float x = event.getX();
		float y = event.getY();
		long touchStartTime = 0;

		// If a touch is moved on the screen
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			handleTouchMove(x, y);
			// A press on the screen
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchStartTime = System.currentTimeMillis();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (System.currentTimeMillis() - touchStartTime < 250L) {
				handleTouchTap(x, y);
			}
		}

		// Remember the values
		oldX = x;
		oldY = y;

		// We handled the event
		return true;
	}

	private void handleTouchTap(float x, float y) {
		// create an box and throw it!
		System.out.println("mouse click!");

	}

	private void handleTouchMove(float x, float y) {
		// Calculate the change
		float dx = x - oldX;
		float dy = y - oldY;

		// Calculate the horizontal and vertical offset since last drag event
		float xoffset = dx; // (x-width/2)/(width/2);
		float yoffset = -dy; // (height/2-y)/(height/2);
	}

}
