package cs155.pong_evolution.views;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import cs155.opengl.R;
import cs155.pong_evolution.model.BallModel;
import cs155.pong_evolution.model.GameModel;
import cs155.pong_evolution.model.MovingObjectModel;
import cs155.pong_evolution.model.PaddleModel;
import cs155.pong_evolution.shapes.Cube;
import cs155.pong_evolution.shapes.Plane;
import cs155.pong_evolution.shapes.Sphere;
import cs155.pong_evolution.shapes.TrianglePrism;
import cs155.pong_evolution.views.MasterView.ViewDelegate;
import android.content.Context;
import android.opengl.GLU;

/**
 * This is a modification of the "Lesson 07: Texture Mapping" NeHe tutorial for
 * the Google Android Platform orginally ported to Android by Savas Ziplies
 * (nea/INsanityDesign)
 * 
 * This file contains the View and Controller components of the game. The Model
 * is in the GameModel class
 * 
 * @author Tim Hickey
 * @author Georg Konwisser, gekonwi@brandeis.edu
 */
public class View10 implements ViewDelegate {

	private static final float PADDLE_HEIGHT = 15f;
	private static final float BALL_SIZE = 5f;

	private Cube cube;
//	private Sphere sphere;
	private Plane floor;
	// private MeshObject suzanne = null;
	private Plane leftWall, rightWall;
	private TrianglePrism tprism;

	private float width, height;

	private int filter = 1; // Which texture filter? ( NEW )

	/*
	 * The initial light values for ambient and diffuse as well as the light
	 * position ( NEW )
	 */
	private float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
	private float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] light0Position = { 50.0f, 25.0f, 50.0f, 1.0f };
	private float[] light1Position = { 50.0f, 10.0f, 50.0f, 1.0f };
	private float[] light2Position = { 50.0f, 5.0f, 50.0f, 1.0f };

	/* The buffers for our light values ( NEW ) */
	private FloatBuffer lightAmbientBuffer;
	private FloatBuffer lightDiffuseBuffer;
	private FloatBuffer lightPositionBuffer;
	private FloatBuffer lightPositionBuffer1;
	private FloatBuffer lightPositionBuffer2;

	/**
	 * Instance the Cube object and set the Activity Context handed over.
	 * Initiate the light buffers and set this class as renderer for this now
	 * GLSurfaceView. Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen and Buttons
	 * 
	 * @param context
	 *            - The Activity Context
	 */
	public View10() {
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

		byteBuf = ByteBuffer.allocateDirect(light0Position.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(light0Position);
		lightPositionBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(light0Position.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer1 = byteBuf.asFloatBuffer();
		lightPositionBuffer1.put(light1Position);
		lightPositionBuffer1.position(0);

		byteBuf = ByteBuffer.allocateDirect(light0Position.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer2 = byteBuf.asFloatBuffer();
		lightPositionBuffer2.put(light2Position);
		lightPositionBuffer2.position(0);

		//
		cube = new Cube();
//		sphere = new Sphere(1f, 30, 30);
		floor = new Plane();
		leftWall = new Plane();
		rightWall = new Plane();
		tprism = new TrianglePrism();
	}

	private void initModel() {
		GameModel game = GameModel.get();

		game.getUserPaddle().setSize(1, PADDLE_HEIGHT);
		game.getAIPaddle().setSize(1, PADDLE_HEIGHT);

		for (int i = 0; i < 3; i++)
			game.getBall().setSize(i, BALL_SIZE);
	}

	/**
	 * The Surface is created/init()
	 */
	public void init(GL10 gl, Context context) {
		initModel();
		
		setupLights(gl);

		/*
		 * general light settings
		 */
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_DITHER); // Disable dithering ( NEW )
		gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0f, 0f, 0f, 1f); // black Background
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		loadTextures(gl, context);
	}

	private void setupLights(GL10 gl) {
		/*
		 * light 0
		 */
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);
		gl.glEnable(GL10.GL_LIGHT0);

		/*
		 * light 1
		 */
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientBuffer);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuseBuffer);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPositionBuffer1);
		gl.glEnable(GL10.GL_LIGHT1);

		/*
		 * light 2
		 */
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, lightAmbientBuffer);
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, lightDiffuseBuffer);
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, lightPositionBuffer2);
		gl.glEnable(GL10.GL_LIGHT2);
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

		setAngleViewFromUser(gl);

		gl.glLoadIdentity(); // Reset The Modelview Matrix
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		// Clear Screen And Depth Buffer
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		setAngleViewFromUser(gl);

		drawBoard(gl);
		drawWalls(gl);

		drawBall(gl, GameModel.get().getBall());
		drawPaddle(gl, GameModel.get().getUserPaddle());
		drawPaddle(gl, GameModel.get().getAIPaddle());
	}

	private void drawWalls(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(GameModel.get().getWidth() / 2, 0f, GameModel.get()
				.getHeight() / 2);

		gl.glPushMatrix();
		gl.glRotatef(90, 0f, 1f, 0f);
		drawWall(gl, rightWall);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(270, 0f, 1f, 0f);
		drawWall(gl, leftWall);
		gl.glPopMatrix();

		gl.glPopMatrix();
	}

	private void drawWall(GL10 gl, Plane wall) {
		GameModel game = GameModel.get();
		
		gl.glPushMatrix();
		gl.glTranslatef(-game.getWidth() / 2f, 0f, -game.getHeight() / 2);
		gl.glRotatef(-90, 1f, 0f, 0f);
		gl.glScalef(game.getWidth(), 1f, game.getHeight() / 10);
		/*
		 * gl.glMatrixMode(GL10.GL_TEXTURE); gl.glPushMatrix();
		 * gl.glRotatef(90f,0f,0f,1f); gl.glPopMatrix();
		 * gl.glMatrixMode(GL10.GL_MODELVIEW);
		 */
		wall.draw(gl, filter);
		gl.glPopMatrix();
	}

	private void translateAndScale(GL10 gl, MovingObjectModel model) {
		float pos[] = model.getCenter();
		gl.glTranslatef(pos[0], pos[1], pos[2]);

		float[] size = model.getSize();
		gl.glScalef(size[0], size[1], size[2]);
	}

	private void drawBall(GL10 gl, BallModel ball) {
		gl.glPushMatrix();

		translateAndScale(gl, ball);
		
		cube.draw(gl, filter);

		gl.glPopMatrix();
	}

	/**
	 * @author Ted
	 * @param gl
	 */
	private void drawPaddle(GL10 gl, PaddleModel paddle) {
		gl.glPushMatrix();

		translateAndScale(gl, paddle);

		gl.glColor4f(1f, 1f, 1f, 1f); // draw in white

		cube.draw(gl, filter);

		gl.glPopMatrix();
	}

	private void setAngleViewFromUser(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Set the properties of the camera
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		GameModel game = GameModel.get();
		
		// Point and aim the camera
		float[] eye = { game.getWidth() / 2f, 100f, game.getHeight() + 200f };
		float[] center = { game.getWidth() / 2f, 0f, game.getHeight() / 2f };
		float[] up = { 0f, 1f, 0f };

		GLU.gluLookAt(gl, eye[0], eye[1], eye[2], center[0], center[1],
				center[2], up[0], up[1], up[2]);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
	}

	// private double calcHeading(BallModel ball) {
	// float x = ball.getDirection()[0];
	// float z = ball.getDirection()[2];
	// double heading = Math.atan2(x, z) / Math.PI * 180;
	// return heading;
	// }

	private void drawBoard(GL10 gl) {
		GameModel game = GameModel.get();
		
		gl.glPushMatrix();
		gl.glScalef(game.getWidth(), 1f, game.getHeight());
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glScalef(8f, 1f, 0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		floor.draw(gl, filter);
		
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
	}

	private void loadTextures(GL10 gl, Context context) {
		/*
		 * Load the textures for the shapes once during Surface creation
		 */
		cube.loadGLTexture(gl, context, R.drawable.wood);
//		sphere.loadGLTexture(gl, context, R.drawable.ball_8);
		floor.loadGLTexture(gl, context, R.drawable.tiles);
		// suzanne.loadGLTexture(gl, context,R.drawable.bg);
		leftWall.loadGLTexture(gl, context, R.drawable.crate);
		rightWall.loadGLTexture(gl, context, R.drawable.crate);
		tprism.loadGLTexture(gl, context, R.drawable.icon);
	}
}