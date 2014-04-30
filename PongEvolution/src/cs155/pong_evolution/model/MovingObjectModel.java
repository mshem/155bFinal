package cs155.pong_evolution.model;

import java.util.Arrays;

/**
 * An abstract model for all objects which are moving on the board.
 * 
 * @author Georg Konwisser, gekonwi@brandeis.edu
 */
public abstract class MovingObjectModel {

	/**
	 * The current movement direction (x, y, z). Make sure the length of this
	 * vector is always <code>1</code>.
	 */
	protected float[] direction;

	/**
	 * the general moving speed of the paddle
	 */
	private float speed;

	/**
	 * the x, y, z coordinates
	 */
	protected float[] center;

	/**
	 * the paddle's size in x, y, z dimensions
	 */
	private float[] size;

	protected GameModel game;

	protected final float[] MIN_CENTER;
	protected final float[] MAX_CENTER;

	/**
	 * Creates a movable object which changes its position on update if
	 * <code>direction != (0, 0, 0)</code>. Initially the object does not move.
	 * 
	 * @param game
	 *            the game model
	 * @param center
	 *            the center of the object (x, y, z)
	 * @param size
	 *            the size of the object (x, y, z)
	 * @param speed
	 *            how fast the object will move if it is not still (field units
	 *            per second)
	 */
	public MovingObjectModel(GameModel game, float[] center, float[] size,
			float speed) {
		this.game = game;
		this.center = center;
		this.size = size;
		this.speed = speed;
		this.direction = new float[] { 0f, 0f, 0f };

		MIN_CENTER = new float[] { size[0] / 2f, center[1], size[2] / 2f };
		MAX_CENTER = new float[] { game.getWidth() - size[0] / 2f, center[1],
				game.getHeight() - size[2] / 2f };
	}

	/**
	 * Update the object's position. Make sure it does not leave the board.
	 */
	public void update() {

		// first update the center position regardless of the board borders
		for (int i = 0; i < 3; i++)
			center[i] += direction[i] * speed * game.getPassedMillis();

		// then check if object is about to leave the board
		boolean atBorder = false;
		for (int i = 0; i < 3; i++) {
			if (center[i] < MIN_CENTER[i] || center[i] > MAX_CENTER[i]) {
				atBorder = true;
				center[i] = Math.min(MAX_CENTER[i], center[i]);
				center[i] = Math.max(MIN_CENTER[i], center[i]);
			}
		}

		if (atBorder)
			reachedBorder();
	}

	/**
	 * handle the situation at which the object reaches one of the four board
	 * borders
	 */
	abstract protected void reachedBorder();

	public float[] getCenter() {
		return center;
	}

	public float[] getSize() {
		return size;
	}

	/**
	 * Returns the maximum coordinate in the given direction which is covered by
	 * this object.
	 * 
	 * @param dimension
	 *            0 for x, 1, for y, 2 for z
	 * @return
	 */
	public float getMaxPos(int dimension) {
		return center[dimension] + size[dimension] / 2f;
	}

	/**
	 * Returns the minimum coordinate in the given direction which is covered by
	 * this object.
	 * 
	 * @param dimension
	 *            0 for x, 1, for y, 2 for z
	 * @return
	 */
	public float getMinPos(int dimension) {
		return center[dimension] - size[dimension] / 2f;
	}
	
	public void setSize(int dimension, float value) {
		this.size[dimension] = value;
	}
	
	public void setCenter(int dimension, float value) {
		this.center[dimension] = value;
	}
}
