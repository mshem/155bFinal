package cs155.pong_evolution.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cs155.pong_evolution.controller.TouchControl;
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

	private int filter = 1;

	private GameModel game;

	private TouchControl touchControl;
	
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
		this.game = new GameModel();

		// Set this as Renderer
		this.setRenderer(this);

		// Request focus, otherwise buttons won't react
		this.requestFocus();
		this.setFocusableInTouchMode(true);

		this.square = new Square3D();
		
		this.touchControl = new TouchControl(game);
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

//		setViewFromAbove(gl);
		setViewFromAboveOrtho(gl);

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
	
	private void setViewFromAboveOrtho(GL10 gl){
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		// Set the properties of the camera. we want an ortho view
//		GLU.gluOrtho2D(gl, 10f, -10f, 10f, -10f);
//		GLU.gluPerspective(gl, 90.0f, width / height, 0.1f, 10000.0f);
		gl.glOrthof(-10f, game.getWidth() + 10f, 0f, 210f, -1f, 1000f);
		gl.glTranslatef(0,20f, 0f);
		gl.glRotatef(90f, 1, 0, 0);
		gl.glTranslatef(0,-30f, -185f);
		// Point and aim the camera
//		GLU.gluLookAt(gl, 
//				game.width/2f, 60f, game.height/2f,              // eye position above Left of game board
//				     game.width/2f, 0f, game.height/2f,   // target position at center of board
//				      1f, 0f, 0f);                         // up direction

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
	}
	

	private void setViewFromAbove(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Set the properties of the camera
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		// Point and aim the camera
		float[] eye = { game.getWidth() / 2f, 250f, game.getHeight() / 2f };
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
	
	/**
	 * Override the touch screen listener.
	 * 
	 * React to moves and presses on the touchscreen.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return touchControl.onTouchEvent(event);
	}

}
