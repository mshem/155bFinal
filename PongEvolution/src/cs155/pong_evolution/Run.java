package cs155.pong_evolution;

import cs155.pong_evolution.views.View0;
import android.app.Activity;
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
	private View0 pa07;

	/**
	 * Initiate our @see Lesson07.java,
	 * which is GLSurfaceView and Renderer
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Initiate our Lesson with this Activity Context handed over
		pa07 = new View0(this);
		//Set the lesson as View to the Activity
		setContentView(pa07);
	}

	/**
	 * Remember to resume our Lesson
	 */
	@Override
	protected void onResume() {
		super.onResume();
		pa07.onResume();
	}

	/**
	 * Also pause our Lesson
	 */
	@Override
	protected void onPause() {
		super.onPause();
		pa07.onPause();
	}

}