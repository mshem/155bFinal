package cs155.pong_evolution.controller;

import cs155.pong_evolution.model.GameModel;
import android.view.MotionEvent;

public class TouchControl {
	private static final long MAX_TAP_MILLIS = 250L;
	private static final float MIN_MOVE_DIST = 2f;
	
	private static final int ACTION_NONE = 0;
	private static final int ACTION_TAP = 1;
	private static final int ACTION_MOVE = 2;

	private int currentAction = ACTION_NONE;

	/*
	 * These variables store the previous X and Y values as well as a fix touch
	 * scale factor. These are necessary for the rotation transformation added
	 * to this lesson, based on the screen touches. ( NEW )
	 */
	private float oldX;
	private float oldY;

	private long touchStartMillis;

	public TouchControl() {
		super();
	}

	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			handleMoveTouch(x, y);

		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchStartMillis = System.currentTimeMillis();

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (currentAction == ACTION_NONE)
				handleTapTouch(x, y);
			currentAction = ACTION_NONE;
		}

		// Remember the values
		oldX = x;
		oldY = y;

		// We handled the event
		return true;
	}

	private void handleTapTouch(float x, float y) {
		if (System.currentTimeMillis() - touchStartMillis > MAX_TAP_MILLIS)
			return;

		// create an box and throw it!
		System.out.println("tapped at (" + x + ", " + y + ")");

		currentAction = ACTION_TAP;

		GameModel.get().getUserPaddle().stop();
	}

	private void handleMoveTouch(float x, float y) {
		// Calculate the change
		float dx = x - oldX;
		float dy = y - oldY;

		if (Math.abs(dx) < MIN_MOVE_DIST)
			return;
		
		System.out.println("dragged by (" + dx + ", " + dy + ")");
		currentAction = ACTION_MOVE;

		if (dx > 0)
			GameModel.get().getUserPaddle().moveRight();
		else
			GameModel.get().getUserPaddle().moveLeft();
	}
}
