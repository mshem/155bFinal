package cs155.pong_evolution.model;

public class GameModel {
	/*
	 * size of the board
	 */
	private int width = 100;
	private int height = 200;

	private BallModel ball;
	
	private int playerScore;
	private int aiScore;

	private PaddleModel userPaddle;
	private PaddleModel aiPaddle;

	private long lastTime = System.currentTimeMillis();
	private long passedMillis = 0;


	public GameModel() {
		this.ball = createBall();
		
		float paddleOffset = 10f;
		userPaddle = createPaddle(height - paddleOffset);
		userPaddle.setName("userPaddle");
		aiPaddle = createPaddle(paddleOffset);
		aiPaddle.setName("aiPaddle");
	}

	private PaddleModel createPaddle(float zPos) {
		float[] center = {width / 2f, 0.1f, zPos};
		float[] size = {20f, 0f, 2f};
		return new PaddleModel(this, center, size, 0.001f);		
	}
	
	private BallModel createBall() {
		float[] center = {width / 2f, 0.1f, height / 2f};
		float[] size = {5f, 0f, 5f};
		return new BallModel(this, center, size, 0.05f);
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

	public void updateScore(PaddleModel w){
		w.setScore(w.getScore()+1);
		System.out.println(w.toString()+" scored! "+w.toString()+" has "+w.getScore()+" points.");
	}
	
	public void resetField(){ //win paddle and lose paddle
		aiPaddle.center[0] = width / 2f;
		userPaddle.center[0] = width/2f;
		ball.center[0] = width/2f;
		ball.center[2] = height/2f;
	}
}
