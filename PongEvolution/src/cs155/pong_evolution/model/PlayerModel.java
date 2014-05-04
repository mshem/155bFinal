package cs155.pong_evolution.model;

import java.util.Observable;

public abstract class PlayerModel extends Observable {
	private String name;
	private int score;
	private PaddleModel paddle;

	public PlayerModel(String name, PaddleModel paddle) {
		super();
		this.name = name;
		this.paddle = paddle;
		this.score = 0;
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
		score++;
		this.setChanged();
		this.notifyObservers();
	}
	
	@Override
	public String toString() {
		return "Player " + name;
	}
}
