package cs155.pong_evolution.model;

import java.util.Random;

public class BallModel extends MovingObjectModel {

	private static final float MIN_PADDLE_HIT_DIST = -1f;
	private static final float MAX_PADDLE_HIT_DIST = 0.5f;

	/**
	 * Defines how much flexibility there is for the initial ball direction.
	 * Should be inside of <code>(0, 90)</code>. <code>0</code> would mean the
	 * ball can only go straight towards the players (which would cause a very
	 * boring game), <code>80</code> means the ball could go straight towards
	 * the player or anywhere between +- 40° compared to this line.
	 */
	private static final float INIT_ANGLE_SPECTRUM = 45f;

	public BallModel(float[] center, float[] size, float speed) {
		super(center, size, speed);

		 randomizeDirection();
		// direction[2] = 1f;
		// center[0] -= 16.5f;
//		setDirection(0);
	}

	private void randomizeDirection() {
		Random rand = new Random();

		boolean towardsUser = rand.nextBoolean();
		double angle = 90.0 - INIT_ANGLE_SPECTRUM / 2;
		angle += rand.nextFloat() * INIT_ANGLE_SPECTRUM;
		
		if (!towardsUser)
			angle = -angle;
		
		setDirection(angle);
	}

	/**
	 * Perform a rotation of the vector (1, 0, 0) by <code>angle</code> degrees
	 * and set the result as the ball's direction in the x-z plane. E.g. an
	 * <code>angle == 0</code> means moving right, <code>angle == 90</code>
	 * means moving straight towards the user, <code>angle == -90</code> means
	 * moving straight towards the AI player.
	 * 
	 * @param angle
	 *            in degrees
	 */
	private void setDirection(double angle) {
		direction[0] = 1;
		direction[2] = 0;

		rotate(angle);
	}

	/**
	 * Adds the given <code>angle</code> to the ball's current direction.
	 * 
	 * @param angle
	 *            in degrees (can be negative)
	 */
	private void rotate(double angle) {
		// convert to radians
		angle = angle / 180.0 * Math.PI;

		float x = direction[0];
		float z = direction[2];
		direction[0] = (float) (x * Math.cos(angle) - z * Math.sin(angle));
		direction[2] = (float) (x * Math.sin(angle) + z * Math.cos(angle));
	}

	@Override
	protected void reachedBorder() {
		// bump from left / right border
		if (getCenter()[0] == minCenter[0] || getCenter()[0] == maxCenter[0])
			direction[0] = -direction[0];

		// for now: also bump from the top and bottom border
		GameModel game = GameModel.get();

		if (getCenter()[2] == minCenter[2]) {
			game.getUserPlayer().increaseScore();
			direction[2] = -direction[2];
			System.out.println("Player Scored! Score:"
					+ game.getUserPlayer().getScore());
		}

		if (getCenter()[2] == maxCenter[2]) {
			game.getAiPlayer().increaseScore();
			direction[2] = -direction[2];
			System.out.println("Computer Scored! Score:"
					+ game.getAiPlayer().getScore());
		}
	}

	public void update() {
		super.update();
		checkAIPaddle();
		checkUserPaddle();
	}

	private boolean passedLeftRight(PaddleModel paddle) {
		if (getMaxPos(0) < paddle.getMinPos(0))
			return true; // missed paddle on the left

		if (getMinPos(0) > paddle.getMaxPos(0))
			return true; // missed paddle on the right

		return false;
	}

	private void checkPaddleDist(float paddleDist, String userName) {
		if (paddleDist < MIN_PADDLE_HIT_DIST)
			return; // ball already passed the top paddle

		if (paddleDist < MAX_PADDLE_HIT_DIST) {
			System.out.println("hit " + userName + " paddle, dist: "
					+ paddleDist);
			direction[2] = -direction[2];
		}
	}

	private void checkAIPaddle() {
		if (direction[2] > 0)
			return; // ball is going away from paddle

		PaddleModel paddle = GameModel.get().getAIPaddle();
		if (passedLeftRight(paddle))
			return;

		float paddleDist = getMinPos(2) - paddle.getMaxPos(2);

		checkPaddleDist(paddleDist, "AI");
	}

	private void checkUserPaddle() {
		if (direction[2] < 0)
			return; // ball is going away from paddle

		PaddleModel paddle = GameModel.get().getUserPaddle();
		if (passedLeftRight(paddle))
			return;

		float paddleDist = paddle.getMinPos(2) - getMaxPos(2);

		checkPaddleDist(paddleDist, "user");
	}
}
