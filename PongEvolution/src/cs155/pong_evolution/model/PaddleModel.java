/**
 * 
 */
package cs155.pong_evolution.model;

import java.util.Random;


public class PaddleModel {
	
	public float dt=0.1f;
	
	public float speed=1f;
	public float[] vel = {0f,0f,1f};
	public float[] pos = {0f,0f,0f};
	public float[] size= {10f,10f,50f};
	
	protected Random rand = new Random();
	protected GameModel game;
	
	public PaddleModel(float speed,float xpos, float zpos, GameModel game){
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
	}
	
	/**
	 * change the velocity slightly and uses it to update the position
	 */
	public void update(){
		pos[0] += vel[0]*dt;
		pos[1] += vel[1]*dt;
		pos[2] += vel[2]*dt;
		
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
