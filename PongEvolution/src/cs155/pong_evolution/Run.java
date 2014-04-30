package cs155.pong_evolution;

import cs155.pong_evolution.views.View0;
import cs155.pong_evolution.views.View1;
import cs155.pong_evolution.views.View10;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * This is the initial Android Activity, setting and initiating
 * the OpenGL ES Renderer Class @see Lesson07.java originally created by
 * Savas Ziplies (nea/INsanityDesign)
 * 
 * @author Tim Hickey
 */
public class Run extends Activity {

	/** Our own OpenGL View overridden */
	private GLSurfaceView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = new View0(this);		
//		view = new View1(this);		
//		view = new View10(this);		
		setContentView(view);
	}

	/**
	 * Remember to resume our Lesson
	 */
	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}

	/**
	 * Also pause our Lesson
	 */
	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
	}

}