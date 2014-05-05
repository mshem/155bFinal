package cs155.pong_evolution;

import cs155.pong_evolution.controller.ScoreListener;
import cs155.pong_evolution.model.GameModel;
import cs155.pong_evolution.views.*;
import cs155.pong_evolution.views.MasterView.ViewDelegate;
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
	private MasterView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = new MasterView(this, new View0());
		ViewDelegate[] nextDelegates = {new View1(), new View2(), new View3(), new View6(), new View10()};
		
		ScoreListener listener = new ScoreListener(view, nextDelegates);
		
		GameModel.get().getUserPlayer().addObserver(listener);
		
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