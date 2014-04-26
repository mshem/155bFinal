package cs155.opengl;

public class Projectile extends Foe {
	
	private float y_acceleration = -1;
	public long birth;
	public boolean active = false;

	public Projectile(float speed, float xpos, float zpos, GameModel07 game) {
		super(speed, xpos, zpos, game);
		this.pos[0]=xpos;
		this.pos[1]=1f;
		this.pos[2]=zpos;
		this.vel[1]=1f;
		birth = game.currentTime;
		active = true;
	}
	
	
	/**
	 * use velocity to update the position, keep on board...
	 * let gravity pull object back down to floor
	 */
	public void update(){
		vel[1] += y_acceleration*dt;

		pos[0] += vel[0]*dt;
		pos[1] += vel[1]*dt;
		pos[2] += vel[2]*dt;
		
		if (pos[1]<0){
			pos[1]=0f;
			y_acceleration=0f;
		}
		//System.out.println("age ="+(game.currentTime-birth)/1000f);
		//System.out.println("proj pos ="+this.pos[0]+","+this.pos[1]+","+this.pos[2]);
		
		keepOnBoard();
	}
	

}
