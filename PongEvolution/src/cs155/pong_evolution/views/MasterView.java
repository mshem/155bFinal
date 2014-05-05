package cs155.pong_evolution.views;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cs155.opengl.R;
import cs155.pong_evolution.controller.TouchControl;
import cs155.pong_evolution.model.BallModel;
import cs155.pong_evolution.model.GameModel;
import cs155.pong_evolution.model.MovingObjectModel;
import cs155.pong_evolution.model.PaddleModel;
import cs155.pong_evolution.shapes.Cube;
import cs155.pong_evolution.shapes.Plane;
import cs155.pong_evolution.shapes.TrianglePrism;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

/**
 * This file is the game's view. It contains the game model and delegates touch
 * events to the controller.
 * 
 * The actual drawing is done by the current {@link ViewDelegate}. This delegate
 * can be changed to change the appearance.
 * 
 * @author Georg Konwisser, gekonwi@brandeis.edu and Michael Shemesh
 */
public class MasterView extends GLSurfaceView implements Renderer {

	public static final float DEFAULT_CAM_HEIGHT = 250f;
	public GLText gltext;
	
	public static interface ViewDelegate {

		/**
		 * The Surface is created/init()
		 */
		public void init(GL10 gl, Context context);

		/**
		 * If the surface changes, reset the viewport
		 */
		public void onSurfaceChanged(GL10 gl, int width, int height);

		/**
		 * Here we do our drawing
		 */
		public void onDrawFrame(GL10 gl);
	}

	private GL10 gl;

	private TouchControl touchControl;

	/** The Activity Context */
	private Context context;

	private ViewDelegate delegate;


	private int width, height;

	/**
	 * Instance the Cube object and set the Activity Context handed over.
	 * Initiate the light buffers and set this class as renderer for this now
	 * GLSurfaceView. Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen and Buttons
	 * 
	 * @param context
	 *            - The Activity Context
	 */
	public MasterView(Context context, ViewDelegate delegate) {
		super(context);
		

		this.context = context;
		
		this.touchControl = new TouchControl();

		// Set this as Renderer
		this.setRenderer(this);

		// Request focus, otherwise buttons won't react
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		
		setDelegate(delegate);
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.gl = gl;
		gltext = new GLText(gl, getContext().getAssets());
		gltext.load("Roboto-Regular.ttf", 14, 2, 2);
		
		initDelegate();
	
	}

	/**
	 * If the surface changes, reset the viewport
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.gl = gl;
		this.width = width;
		this.height = height;
		
		delegate.onSurfaceChanged(gl, width, height);
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		this.gl = gl;
		
		GameModel.get().update();
		
		delegate.onDrawFrame(gl);
		displayScore(gl);
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

	public void setDelegate(ViewDelegate delegate) {
		this.delegate = delegate;
//		gltext.load("Roboto-Regular.ttf", 14, 2, 2);
		if (gl == null)
			return; // haven't been drawn yet
		
		initDelegate();
	}

	private void initDelegate() {
//		gltext.load("Roboto-Regular.ttf", 14, 2, 2);
		delegate.init(gl, context);
		delegate.onSurfaceChanged(gl, width, height);
	}
	
	//Written by Michael Shemesh 
	public void displayScore(GL10 gl){
			gl.glPushMatrix();
			// Redraw background color
//			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

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
			gl.glTranslatef(-10f, -30f, 0f);
//			gl.glTranslatef(-20f, -20f, 0f);
			// TEST: render the entire font texture
			gl.glColor4f(0f, 1.0f, 0f, 1f); // Set Color to Use
			gltext.drawTexture(width, height); // Draw the Entire Texture

			GameModel game = GameModel.get();

			// Display ai score
			gltext.begin(0f, 0f, 0f, 1.0f); // Begin Text Rendering (Set Color
													// WHITE)
			gltext.draw("" + game.getAiPlayer().getScore(), 0, 0); // Draw Test
																	// String
			gltext.end();

			// display player score
			gl.glTranslatef(0f, -160f, 0f);

			gltext.begin(0f, 0f, 0f, 1.0f); // Begin Text Rendering (Set Color
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
//			gl.glDisable(GL10.GL_TEXTURE_2D); // Disable Texture Mapping
			gl.glPopMatrix();
		
	}
}