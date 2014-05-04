/**
 * 
 */
package cs155.pong_evolution.model;

/**
 * 
 * 
 * @author Georg Konwisser, gekonwi@brandeis.edu
 */
public class AiPlayerModel extends PlayerModel {

	private final long MAX_REACTION_MILLIS = 3000;
	private final long REACTION_MILLIS_IMPROVEMENT = 300;
	private final long REACTION_MILLIS;

	private long lastActionMillis = System.currentTimeMillis();

	/**
	 * Create an AI player who controls its paddle.
	 * 
	 * @param paddle
	 * @param strength
	 *            bigger value means stronger AI opponent (max: <code>10</code>)
	 */
	public AiPlayerModel(PaddleModel paddle, int strength) {
		super("AI", paddle);

		REACTION_MILLIS = MAX_REACTION_MILLIS - REACTION_MILLIS_IMPROVEMENT
				* strength;
	}

	@Override
	public void update() {
		getPaddle().update();
		
		if (System.currentTimeMillis() - lastActionMillis < REACTION_MILLIS)
			return;

		lastActionMillis = System.currentTimeMillis();

		float expectedHitPosX = getExpectedHitPosX();
		if (expectedHitPosX > getPaddle().getMinPos(0) && expectedHitPosX < getPaddle().getMaxPos(0))
			getPaddle().stop();
		else if (expectedHitPosX < getPaddle().center[0])
			getPaddle().moveLeft();
		else
			getPaddle().moveRight();
	}

	private float getExpectedHitPosX() {
		BallModel ball = GameModel.get().getBall();

		float zDif = ball.getMaxPos(2) - getPaddle().getMaxPos(2);
		float dt = -zDif / ball.direction[2];

		return ball.center[0] + ball.direction[0] * dt;
	}
}
