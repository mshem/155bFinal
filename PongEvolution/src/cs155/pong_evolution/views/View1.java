package cs155.pong_evolution.views;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import cs155.pong_evolution.controller.TouchControl;
import cs155.pong_evolution.model.BallModel;
import cs155.pong_evolution.model.GameModel;
import cs155.pong_evolution.model.MovingObjectModel;
import cs155.pong_evolution.model.PaddleModel;
import cs155.pong_evolution.shapes.Square3D;
import cs155.pong_evolution.views.MasterView.ViewDelegate;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
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
public class View1 implements ViewDelegate {

	private Square3D square;

	private float width, height;

	private int filter = 1;

	GLText gltext;

	/**
	 * Instance the Cube object and set the Activity Context handed over.
	 * Initiate the light buffers and set this class as renderer for this now
	 * GLSurfaceView. Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen and Buttons
	 */
	public View1() {
		this.square = new Square3D();
	}

	/**
	 * The Surface is created/init()
	 */
	public void init(GL10 gl, Context context) {
		// Settings
		gltext = new GLText(gl, context.getAssets());
		gltext.load("Roboto-Regular.ttf", 14, 2, 2);
		
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
		// Clear Screen And Depth Buffer
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		setViewFromAbove(gl);
		// setViewFromAboveOrtho(gl);
		displayScore(gl);
		drawBoard(gl);

		GameModel game = GameModel.get();

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

	private void setViewFromAboveOrtho(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		gl.glOrthof(-10f, GameModel.get().getWidth() + 10f, 0f, 210f, -1f,
				1000f);
		gl.glTranslatef(0, 20f, 0f);
		gl.glRotatef(90f, 1, 0, 0);
		gl.glTranslatef(0, -30f, -185f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
	}

	private void setViewFromAbove(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Set the properties of the camera
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		GameModel game = GameModel.get();

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

		GameModel game = GameModel.get();

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

	public void displayScore(GL10 gl) {

		gl.glPushMatrix();
		// Redraw background color
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Set to ModelView mode
		gl.glMatrixMode(GL10.GL_MODELVIEW); // Activate Model View Matrix
		gl.glLoadIdentity(); // Load Identity Matrix

		// enable texture + alpha blending
		// NOTE: this is required for text rendering! we could incorporate it
		// into
		// the GLText class, but then it would be called multiple times (which
		// impacts performance).
		gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping
		gl.glEnable(GL10.GL_BLEND); // Enable Alpha Blend
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA); // Set
																		// Alpha
																		// Blend
																		// Function
		gl.glRotatef(-90, 1f, 0f, 0f);
		gl.glTranslatef(-20f, -20f, 0f);
		// TEST: render the entire font texture
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Set Color to Use
		gltext.drawTexture(width, height); // Draw the Entire Texture

		GameModel game = GameModel.get();

		// Display ai score
		gltext.begin(1.0f, 1.0f, 1.0f, 1.0f); // Begin Text Rendering (Set Color
												// WHITE)
		gltext.draw("" + game.getAiPlayer().getScore(), 0, 0); // Draw Test
																// String
		gltext.end();

		// display player score
		gl.glTranslatef(0f, -180f, 0f);

		gltext.begin(1.0f, 1.0f, 1.0f, 1.0f); // Begin Text Rendering (Set Color
												// WHITE)
		gltext.draw("" + game.getUserPlayer().getScore(), 0, 0); // Draw Test
																	// String
		gltext.end();

		// End Text Rendering

		// gltext.begin( 0.0f, 0.0f, 1.0f, 1.0f ); // Begin Text Rendering (Set
		// Color BLUE)
		// gltext.draw( "More Lines...", 50, 150 ); // Draw Test String
		// gltext.draw( "The End.", 50, 150 + gltext.getCharHeight() ); // Draw
		// Test String
		// gltext.end(); // End Text Rendering

		// disable texture + alpha
		gl.glDisable(GL10.GL_BLEND); // Disable Alpha Blend
		gl.glDisable(GL10.GL_TEXTURE_2D); // Disable Texture Mapping
		gl.glPopMatrix();
	}
}
