package cs155.pong_evolution.model;

public class UserPlayerModel extends PlayerModel {

	public UserPlayerModel(PaddleModel paddle) {
		super("User", paddle);
	}

	@Override
	public void update() {
		getPaddle().update();
	}
	
}
