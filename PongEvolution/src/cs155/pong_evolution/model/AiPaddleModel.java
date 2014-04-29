/**
 * 
 */
package cs155.pong_evolution.model;

/**
 * 
 * 
 * @author Georg Konwisser, gekonwi@brandeis.edu
 */
public class AiPaddleModel extends PaddleModel {

	private final long MAX_REACTION_MILLIS = 3000;
	private final long REACTION_MILLIS_IMPROVEMENT = 300;
	private final long REACTION_MILLIS;

	private long lastActionMillis = System.currentTimeMillis();

	/**
	 * Creates a paddle controlled by an AI opponent.
	 * 
	 * @param strength
	 *            bigger value means stronger AI opponent (max: <code>10</code>)
	 * @param game
	 * @param center
	 * @param size
	 * @param speed
	 */
	public AiPaddleModel(int strength, GameModel game, float[] center,
			float[] size, float speed) {
		super(game, center, size, speed);

		REACTION_MILLIS = MAX_REACTION_MILLIS - REACTION_MILLIS_IMPROVEMENT
				* strength;
	}

	@Override
	public void update() {
		super.update();

		if (System.currentTimeMillis() - lastActionMillis < REACTION_MILLIS)
			return;

		lastActionMillis = System.currentTimeMillis();

		float expectedHitPosX = getExpectedHitPosX();
		if (expectedHitPosX > getMinPos(0) && expectedHitPosX < getMaxPos(0))
			stop();
		else if (expectedHitPosX < center[0])
			moveLeft();
		else
			moveRight();
	}

	private float getExpectedHitPosX() {
		BallModel ball = game.getBall();

		float zDif = ball.getMaxPos(2) - getMaxPos(2);
		float dt = -zDif / ball.direction[2];

		return ball.center[0] + ball.direction[0] * dt;
	}
}
