package cs155.pong_evolution.model;

public class GameModel {
	private static final float BALL_SPEED = 0.05f;
	private static final float PADDLE_SPEED = 0.05f;
	private static final int AI_STRENGTH = 2;
	/*
	 * size of the board
	 */
	private int width = 100;
	private int height = 200;

	private BallModel ball;

	private PaddleModel userPaddle;
	private PaddleModel aiPaddle;

	private long lastTime = System.currentTimeMillis();
	private long passedMillis = 0;

	public GameModel() {
		this.ball = createBall();
		
		float paddleOffset = 10f;
		userPaddle = createPaddle(height - paddleOffset, false);
		aiPaddle = createPaddle(paddleOffset, true);
	}

	private PaddleModel createPaddle(float zPos, boolean ai) {
		float[] center = {width / 2f, 0.1f, zPos};
		float[] size = {20f, 0f, 2f};
		if (ai)
			return new AiPaddleModel(AI_STRENGTH, this, center, size, PADDLE_SPEED);
		else
			return new PaddleModel(this, center, size, PADDLE_SPEED);		
	}
	
	private BallModel createBall() {
		float[] center = {width / 2f, 0.1f, height / 2f};
		float[] size = {5f, 0f, 5f};
		return new BallModel(this, center, size, BALL_SPEED);
	}

	public BallModel getBall() {
		return ball;
	}

	public void setBall(BallModel ball) {
		this.ball = ball;
	}

	public PaddleModel getUserPaddle() {
		return userPaddle;
	}

	public void setUserPaddle(PaddleModel paddle) {
		userPaddle = paddle;
	}

	public PaddleModel getAIPaddle() {
		return aiPaddle;
	}

	public void setAIPaddle(PaddleModel paddle) {
		aiPaddle = paddle;
	}

	public void update() {
		long currentTime = System.currentTimeMillis();
		passedMillis = currentTime - lastTime;
		lastTime = currentTime;

		ball.update();
		userPaddle.update();
		aiPaddle.update();
	}

	public long getPassedMillis() {
		return passedMillis;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}	
}
