/**
 * 
 */
package cs155.pong_evolution.controller;

import java.util.Observable;
import java.util.Observer;

import cs155.pong_evolution.model.GameModel;
import cs155.pong_evolution.views.MasterView;
import cs155.pong_evolution.views.MasterView.ViewDelegate;
import cs155.pong_evolution.views.View0;
import cs155.pong_evolution.views.View1;
import cs155.pong_evolution.views.View10;

/**
 * This listener is meant to listen for score changes in order to switch the
 * level.
 * 
 * @author Georg Konwisser, gekonwi@brandeis.edu
 * 
 */
public class ScoreListener implements Observer {

	private MasterView view;
	private ViewDelegate[] delegates;
	private int delegateIndex;
	
	/**
	 * The score difference after which to switch the view.
	 */
	private static final int VIEW_SWITCH_SCORE = 1;
	
	public ScoreListener(MasterView view, ViewDelegate[] delegates) {
		super();
		this.view = view;
		this.delegates = delegates;
		
		delegateIndex = -1;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		int userScore = GameModel.get().getUserPlayer().getScore();
		if (userScore % VIEW_SWITCH_SCORE != 0)
			return;
		
		if (delegateIndex == delegates.length - 1)
			return;
		
		delegateIndex++;
		view.setDelegate(delegates[delegateIndex]);
	}
}