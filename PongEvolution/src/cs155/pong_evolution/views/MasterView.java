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
 * @author Georg Konwisser, gekonwi@brandeis.edu
 */
public class MasterView extends GLSurfaceView implements Renderer {

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

		if (gl == null)
			return; // haven't been drawn yet
		
		initDelegate();
	}

	private void initDelegate() {
		delegate.init(gl, context);
		delegate.onSurfaceChanged(gl, width, height);
	}
}