/**
 * 
 */
package cs155.pong_evolution.model;

/**
 * 
 * 
 * @author Ted
 * @author Georg Konwisser, gekonwi@brandeis.edu
 */
public class PaddleModel extends MovingObjectModel {

	public PaddleModel(GameModel game, float[] center, float[] size, float speed) {
		super(game, center, size, speed);

		// initially a paddle does not move
		this.direction = new float[] { 0f, 0f, 0f };
	}

	/**
	 * Start a continuous left movement of the paddle until it reaches the
	 * board's border or {@link #stop()} is called.
	 * 
	 * The panel will move according to it previously set speed.
	 */
	public void moveLeft() {
		this.direction[0] = -1;
	}

	/**
	 * Start a continuous right movement of the paddle until it reaches the
	 * board's border or {@link #stop()} is called.
	 * 
	 * The panel will move according to it previously set speed.
	 */
	public void moveRight() {
		this.direction[0] = 1;
	}

	/**
	 * Stop the panel's movement. It can be started again calling
	 * {@link #moveLeft()} or {@link #moveRight()}.
	 */
	public void stop() {
		this.direction[0] = 0;
	}

	@Override
	protected void reachedBorder() {
		stop();
	}
}
