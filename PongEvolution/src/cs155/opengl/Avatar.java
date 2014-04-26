package cs155.opengl;

import java.util.Random;

/**
 * an avatar is controlled by using the mouse to change the velocity
 * @author tim
 *
 */
public class Avatar extends FoeSmooth {


	public Avatar(float speed,float xpos, float zpos, GameModel07 game){
		super(speed,xpos,zpos,game);
	}
	
	/**
	 * use the velocity to update the position
	 */
	
	public void update(){
		vel[0] = (float) Math.cos(heading*Math.PI/180)*speed;
		vel[2] = (float) Math.sin(heading*Math.PI/180)*speed;
		pos[0] += vel[0]*dt;
		pos[2] += vel[2]*dt;
		//System.out.println("speed = "+speed);

		keepOnBoard();
	}
	
}
