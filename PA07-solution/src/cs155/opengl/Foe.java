package cs155.opengl;

import java.util.Random;

/**
 * a foe is a box that moves around a room
 * it has a current velocity which is randomly changed
 * by small increments without changing its speed.
 * @author tim
 *
 */
public class Foe {
	
	public float dt=0.1f;
	
	public float speed=1f;
	public float[] vel = {0f,0f,1f};
	public float[] pos = {0f,0f,0f};
	public float armAngle = 0; // angle of flapping arms 
	
	protected Random rand = new Random();
	protected GameModel07 game;
	
	public Foe(float speed,float xpos, float zpos, GameModel07 game){
		this.game = game;
		this.pos[0]=  (rand.nextFloat())*game.width; //xpos;
		this.pos[2]=  (rand.nextFloat())*game.height; //zpos;
		this.speed = speed;
		float a = (rand.nextFloat()-0.5f)*2;
		float b = (rand.nextFloat()-0.5f)*2;	
		float c = (float)(Math.sqrt(a*a+b*b));
		
		// look out for the theoretical case of c==0
		if (c==0){a=b=1; c= (float)Math.sqrt(2);}
		vel[0] = a/c*speed;
		vel[2] = b/c*speed;
		

		
		armAngle = (float) (rand.nextFloat()-0.5)*2*30;
	}
	
	/**
	 * change the velocity slightly and uses it to update the position
	 */
	public void update(){
		
		
		 // generate angle (in radians) between -s and s degrees
		double r = (rand.nextGaussian())*Math.PI/180; 
		//if (Math.abs(r)< 0.1) r=0f; // this can smooth out the ride!
		float c1 = (float) Math.cos(r);
		float s1 = (float) Math.sin(r);
		float a = c1*vel[0] - s1*vel[2];
		float b = s1*vel[0] + c1*vel[2];
		vel[0] = a;
		vel[2] = b;
		pos[0] += vel[0]*dt;
		pos[1] += vel[1]*dt;
		pos[2] += vel[2]*dt;
		
		armAngle++;

		
		keepOnBoard();
	}
	
	protected void keepOnBoard(){
		if (pos[0]<0) {
			vel[0] *= -1; pos[0]=0;
		} else if (pos[0] > game.width-1){
			vel[0] *= -1; pos[0] = game.width-1;}
		
		if (pos[2]<0) {
			vel[2] *= -1; pos[2]=0;
		} else if (pos[2] > game.height-1){
			vel[2] *= -1; pos[2] = game.height-1;
		}
	}

}
