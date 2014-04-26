package cs155.opengl;

import java.util.Random;

/**
 * a foe is a box that moves around a room
 * it has a current velocity which is randomly changed
 * by small increments without changing its speed.
 * @author tim
 *
 */
public class FoeSmooth extends Foe {

	public float heading; // the angle in degrees it is moving
	public float angularVelocity; // the speed the angle is changing
	public float maxAngularVelocity = 5f;

	public FoeSmooth(float speed,float xpos, float zpos, GameModel07 game){
		super(speed,xpos,zpos,game);
	}
	
	/**
	 * change the velocity slightly and uses it to update the position
	 */
	
	public void update(){
		 // generate angle (in radians) between -s and s degrees
		double r = (rand.nextGaussian())*Math.PI/180*this.maxAngularVelocity;

		if (r > this.maxAngularVelocity)
			r=this.maxAngularVelocity;
		else if (r < -this.maxAngularVelocity)
			r = -this.maxAngularVelocity;
	
		
		heading += r;
		
		//if (Math.abs(r)< 0.1) r=0f; // this can smooth out the ride!
		vel[0] = (float) Math.cos(heading*Math.PI/180)*speed;
		vel[2] = (float) Math.sin(heading*Math.PI/180)*speed;

		pos[0] += vel[0]*dt;
		pos[2] += vel[2]*dt;
		//System.out.println("heading="+heading);
		//System.out.println("sf pos = "+pos[0]+" "+pos[1]+" "+pos[2]+" ");
		//System.out.println("sf vel = "+vel[0]+" "+vel[1]+" "+vel[2]+" ");
		
		keepOnBoard();
	}
	
	protected void keepOnBoard(){
		pp();
		if (pos[0]<0) {
			//pos[0] += game.width; //
			heading = 180f-heading; pos[0]=0;
		} else if (pos[0] > game.width-1){
			//pos[0] -= game.width; //
			heading=180f-heading; pos[0] = game.width-1;
		}
		if (pos[2]<0) {
			//pos[2] += game.height; //
			heading=-heading; pos[2]=0;
		} else if (pos[2] > game.height-1){
			//pos[2] -= game.height; // 
			heading=-heading; pos[2] = game.height-1;
		}
	}
	
	public void pp(){
		//System.out.println("p="+pos[0]+","+pos[1]+","+pos[2]+"  h="+heading);
	}

}
