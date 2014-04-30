package cs155.pong_evolution.model;

public class GameModel {
	private static final float BALL_SPEED = 0.05f;
	private static final float PADDLE_SPEED = 0.05f;
	private static final float PADDLE_OFFSET = 10f;
	private static final int AI_STRENGTH = 2;
	/*
	 * size of the board
	 */
	private static final int WIDTH = 100;
	private static final int HEIGHT = 200;

	private BallModel ball;

	private PlayerModel userPlayer;
	private PlayerModel aiPlayer;

	private long lastTime = System.currentTimeMillis();
	private long passedMillis = 0;

	public GameModel() {
		this.ball = createBall();

		setUserPlayer(new UserPlayerModel(createPaddle(HEIGHT - PADDLE_OFFSET)));
		setAiPlayer(new AiPlayerModel(createPaddle(PADDLE_OFFSET), AI_STRENGTH));
	}

	private PaddleModel createPaddle(float zPos) {
		float[] center = { WIDTH / 2f, 0.1f, zPos };
		float[] size = { 20f, 0f, 2f };
		return new PaddleModel(this, center, size, PADDLE_SPEED);
	}

	private BallModel createBall() {
		float[] center = { WIDTH / 2f, 0.1f, HEIGHT / 2f };
		float[] size = { 5f, 0f, 5f };
		return new BallModel(this, center, size, BALL_SPEED);
	}

	public BallModel getBall() {
		return ball;
	}

	public void setBall(BallModel ball) {
		this.ball = ball;
	}

	public PaddleModel getUserPaddle() {
		return getUserPlayer().getPaddle();
	}

	public PaddleModel getAIPaddle() {
		return getAiPlayer().getPaddle();
	}

	public void update() {
		long currentTime = System.currentTimeMillis();
		passedMillis = currentTime - lastTime;
		lastTime = currentTime;

		ball.update();
		getUserPlayer().update();
		getAiPlayer().update();
	}

	public long getPassedMillis() {
		return passedMillis;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public PlayerModel getUserPlayer() {
		return userPlayer;
	}

	public void setUserPlayer(PlayerModel userPlayer) {
		this.userPlayer = userPlayer;
	}

	public PlayerModel getAiPlayer() {
		return aiPlayer;
	}

	public void setAiPlayer(PlayerModel aiPlayer) {
		this.aiPlayer = aiPlayer;
	}
}
