package cs155.opengl;

import java.util.ArrayList;

/**
 * This is a demo of a simple game where the
 * avatar throws bombs at the foes from a spot in the
 * corner of a room. The player rotates the view
 * by dragging the mouse and fires by clicking the mouse
 * 
 * This file stores the model which consists of an arraylist
 * of Foes and an arraylist of Bombs. The Foes update their
 * movement by randomly changing their velocity. The Bombs
 * are under the control of gravity
 * @author tim
 *
 */
public class GameModel07 {
	/*
	 * size of the gameboard
	 */
	public int width = 100;
	public int height = 100;
	
	public ArrayList<Foe> foes = new ArrayList<Foe>();
	public Avatar avatar;
	public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	public boolean readyToFire = false;
	
	public long currentTime = System.currentTimeMillis();
	public long lastTime = currentTime;
	public float dt=0;
	
	/**
	 * create the foes and avatar for the game
	 * @param numFoes
	 */
	public GameModel07(int numFoes){
		for(int i=0;i<numFoes;i++){
			foes.add(new Foe(1f,width/2f,height/2f,this));
		}
		avatar = new Avatar(1f,width/2f,height/2f,this);
	}
	
	public void fireProjectile(){
		Projectile p = new Projectile(10f,avatar.pos[0],avatar.pos[2],this);
		projectiles.add(p);
		p.vel = avatar.vel.clone();

		p.vel[0] *= 2f;
		p.vel[1]  = 3f;
		p.vel[2] *= 2f;
	}
	
	public void update(){
		lastTime=currentTime;
		currentTime = System.currentTimeMillis();
		dt = (float)((currentTime-lastTime)/1000);
		
		for(Foe f: foes){
			f.update();
		}
		
		if (readyToFire){
			fireProjectile();
			readyToFire = false;
		}
		
		ArrayList<Projectile> activeProjs = new ArrayList<Projectile>();
		for(Projectile f: projectiles){
			f.update();
			if (currentTime - f.birth > 4000) {
				f.active= false;
			} else {
				activeProjs.add(f);
			}		
		}
		projectiles = activeProjs;
		
		handleCollisions();
		
		avatar.update();
	}
	
	private void handleCollisions(){
		for(Foe f:foes){
			for(Foe g:projectiles){
				if (hitFoeFoe(f,g)){
					f.vel[1]=-0.2f;
					//System.out.println("Collision!!");
					
					continue;
				}
			}
		}
	}
	
	private boolean hitFoeFoe(Foe f, Foe g){
		double d= Math.abs(f.pos[0]-g.pos[0]) + Math.abs(f.pos[2]-g.pos[2]);
		//System.out.println("dist = "+d);
		return (d<1);
		
	}

}
