package cs155.pong_evolution.model;

public abstract class PlayerModel {
	private String name;
	private int score;
	private PaddleModel paddle;

	public PlayerModel(String name, PaddleModel paddle) {
		super();
		this.name = name;
		this.paddle = paddle;
		this.setScore(0);
	}

	public abstract void update();

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public PaddleModel getPaddle() {
		return paddle;
	}

	public void increaseScore() {
		setScore(getScore() + 1);
	}
	
	@Override
	public String toString() {
		return "Player " + name;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
