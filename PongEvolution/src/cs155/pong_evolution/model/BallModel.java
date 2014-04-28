package cs155.pong_evolution.model;

import java.util.Random;

public class BallModel extends MovingObjectModel {

	private static final float PADDLE_HIT_DIST = 0.9f;

	public BallModel(GameModel game, float[] center, float[] size, float speed) {
		super(game, center, size, speed);

		// randomizeDirection();
		direction[2] = 1f;
	}

	private void randomizeDirection() {
		Random rand = new Random();
		float a = (rand.nextFloat() - 0.5f) * 2;
		float b = (rand.nextFloat() - 0.5f) * 2;
		float c = (float) (Math.sqrt(a * a + b * b));

		// look out for the theoretical case of c==0
		if (c == 0) {
			a = b = 1;
			c = (float) Math.sqrt(2);
		}
		direction[0] = a / c;
		direction[2] = b / c;
	}

	@Override
	protected void reachedBorder() {
		// bump from left / right border
		if (getCenter()[0] == MIN_CENTER[0] || getCenter()[0] == MAX_CENTER[0])
			direction[0] = -direction[0];

		// for now: also bump from the top and bottom border
		if (getCenter()[2] == MIN_CENTER[2] || getCenter()[2] == MAX_CENTER[2])
			direction[2] = -direction[2];
	}

	public void update() {
		super.update();
		checkAIPaddle();
		checkUserPaddle();
	}

//	private void checkPaddle(PaddleModel paddle) {
//		float centerXDif = Math.abs(center[0] - paddle.center[0]);
//		float maxCenterXDif = Math.abs(getSize()[0] / 2f + paddle.getSize()[0]
//				/ 2f);
//		 if (centerXDif > maxCenterXDif)
//			 return; // passes the paddle left or right
//	}

	private boolean passedLeftRight(PaddleModel paddle) {
		if (getMaxPos(0) < paddle.getMinPos(0))
			return true; // missed paddle on the left

		if (getMinPos(0) > paddle.getMaxPos(0))
			return true; // missed paddle on the right

		return false;
	}

	private void checkAIPaddle() {
		if (direction[2] > 0)
			return; // ball is going away from paddle

		PaddleModel paddle = game.getAIPaddle();
		if (passedLeftRight(paddle))
			return;

		if (getMinPos(2) < paddle.getMaxPos(2))
			return; // ball already passed the top paddle

		float paddleDist = getMinPos(2) - paddle.getMaxPos(2);
		if (paddleDist < PADDLE_HIT_DIST) {
			System.out.println("hit AI paddle, dist: " + paddleDist);
			direction[2] = -direction[2];
		}
	}

	private void checkUserPaddle() {
		if (direction[2] < 0)
			return; // ball is going away from paddle

		PaddleModel paddle = game.getUserPaddle();
		if (passedLeftRight(paddle))
			return;

		if (getMaxPos(2) > paddle.getMinPos(2))
			return; // ball already passed the bottom paddle

		float paddleDist = paddle.getMinPos(2) - getMaxPos(2);
		if (paddleDist < PADDLE_HIT_DIST) {
			System.out.println("hit user paddle, dist: " + paddleDist);
			direction[2] = -direction[2];
		}
	}
}
