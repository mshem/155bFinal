package cs155.pong_evolution.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cs155.pong_evolution.model.BallModel;
import cs155.pong_evolution.model.GameModel;
import cs155.pong_evolution.model.MovingObjectModel;
import cs155.pong_evolution.model.PaddleModel;
import cs155.pong_evolution.shapes.Square3D;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

/**
 * This is a modification of the "Lesson 07: Texture Mapping" NeHe tutorial for
 * the Google Android Platform originally ported to Android by Savas Ziplies
 * (nea/INsanityDesign)
 * 
 * This file contains the View and Controller components of the game. The Model
 * is in the GameModel07 class
 * 
 * @author Tim Hickey
 */
public class View0 extends GLSurfaceView implements Renderer {

	private Square3D square;

	private float width, height;

	private int filter = 1; // Which texture filter? ( NEW )

	/*
	 * These variables store the previous X and Y values as well as a fix touch
	 * scale factor. These are necessary for the rotation transformation added
	 * to this lesson, based on the screen touches. ( NEW )
	 */
	private float oldX;
	private float oldY;

	private GameModel game;

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

		square = new Square3D();
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Settings
		gl.glDisable(GL10.GL_DITHER); // Disable dithering ( NEW )
		gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0f, 0f, 1.0f, 1f); // White Background
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	/**
	 * If the surface changes, reset the viewport
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) {
			height = 1;
		} // Prevent A Divide By Zero By Making Height Equal One

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

		drawBoard(gl);
		drawBall(gl, game.getBall());
		drawPaddle(gl, game.getUserPaddle());
		drawPaddle(gl, game.getAIPaddle());
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

		gl.glColor4f(1f, 1f, 1f, 1f); // draw in white

		square.draw(gl, filter);

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

		square.draw(gl, filter);

		gl.glPopMatrix();
	}

	private void setViewFromAbove(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Set the properties of the camera
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		// Point and aim the camera
		float[] eye = { game.getWidth() / 2f, 300f, game.getHeight() / 2f };
		float[] center = { game.getWidth() / 2f, 0f, game.getHeight() / 2f };
		float[] up = { 0f, 0f, -1f };

		GLU.gluLookAt(gl, eye[0], eye[1], eye[2], center[0], center[1],
				center[2], up[0], up[1], up[2]);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
	}

	private void drawBoard(GL10 gl) {
		gl.glPushMatrix();

		// move to the board center
		gl.glTranslatef(game.getWidth() / 2f, 0f, game.getHeight() / 2f);
		
		gl.glScalef(game.getWidth(), 1f, game.getHeight());

		gl.glColor4f(0f, 0f, 0f, 1f); // draw in black

		square.draw(gl, filter);

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
		System.out.println("tapped at (" + x + ", " + y + ")");

	}

	private void handleTouchMove(float x, float y) {
		// Calculate the change
		float dx = x - oldX;
		float dy = y - oldY;

		System.out.println("dragged by (" + dx + ", " + dy + ")");
	}

}
