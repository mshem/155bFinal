package cs155.pong_evolution.model;

import java.util.ArrayList;


/**
 * This is a demo of a simple game where the avatar throws bombs at the foes
 * from a spot in the corner of a room. The player rotates the view by dragging
 * the mouse and fires by clicking the mouse
 * 
 * This file stores the model which consists of an arraylist of Foes and an
 * arraylist of Bombs. The Foes update their movement by randomly changing their
 * velocity. The Bombs are under the control of gravity
 * 
 * @author tim
 * 
 */
public class GameModel {
	/*
	 * size of the gameboard
	 */
	public int width = 100;
	public int height = 200;

	private BallModel ball;
	
	private PaddleModel leftPaddle;
	private PaddleModel rightPaddle;
	
	public long lastTime = System.currentTimeMillis();
	public float passedSecs = 0;

	/**
	 * create the foes and avatar for the game
	 * 
	 * @param numFoes
	 */
	public GameModel() {
		ball = new BallModel(10f, 0f, 0f, this);
		leftPaddle= new PaddleModel(0f, 0f, 0f, this);
		rightPaddle= new PaddleModel(0f, 0f, 0f, this);
	}

	public BallModel getBall() {
		return ball;
	}

	public void setBall(BallModel ball) {
		this.ball = ball;
	}
	
	public PaddleModel getLeftPaddle(){
		return leftPaddle;
	}
	
	public void setLeftPaddle(PaddleModel paddle){
		leftPaddle=paddle;
	}
	
	public PaddleModel getRightPaddle(){
		return rightPaddle;
	}
	
	public void setRightPaddle(PaddleModel paddle){
		rightPaddle=paddle;
	}

	public void update() {
		long currentTime = System.currentTimeMillis();
		passedSecs = (float) ((currentTime - lastTime) / 1000);
		lastTime = currentTime;

		ball.update();
		leftPaddle.update();
		rightPaddle.update();
	}
}
