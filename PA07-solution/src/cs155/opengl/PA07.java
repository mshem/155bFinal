package cs155.opengl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cs155.opengl.R;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

/**
 * This is a modification of the "Lesson 07: Texture Mapping"
 * NeHe tutorial for the Google Android Platform orginally
 * ported to Android by Savas Ziplies (nea/INsanityDesign)
 * 
 * This file contains the View and Controller components 
 * of the game. The Model is in the GameModel07 class
 * 
 * @author Tim Hickey
 */
public class PA07 extends GLSurfaceView implements Renderer {
	
	/** Cube instance */
	private Cube cube;	
	private Plane floor;
	private MeshObject suzanne = null;
	private Plane wallN, wallS, wallE, wallW;
	private TrianglePrism tprism;
	
	private float width, height;
	

	
	private int filter = 1;				//Which texture filter? ( NEW )
	
	/** Is light enabled ( NEW ) */
	private boolean light = false;

	/* 
	 * The initial light values for ambient and diffuse
	 * as well as the light position ( NEW ) 
	 */
	private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
	private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
	private float[] lightPosition = {50.0f, 25.0f, 50.0f, 1.0f};
	private float[] lightPosition1 = {50.0f, 10.0f, 50.0f, 1.0f};
	private float[] lightPosition2 = {50.0f, 5.0f, 50.0f, 1.0f};
		
	/* The buffers for our light values ( NEW ) */
	private FloatBuffer lightAmbientBuffer;
	private FloatBuffer lightDiffuseBuffer;
	private FloatBuffer lightPositionBuffer;
	private FloatBuffer lightPositionBuffer1;
	private FloatBuffer lightPositionBuffer2;
	
	/*
	 * These variables store the previous X and Y
	 * values as well as a fix touch scale factor.
	 * These are necessary for the rotation transformation
	 * added to this lesson, based on the screen touches. ( NEW )
	 */
	private float oldX;
    private float oldY;

	private GameModel07 game;
	/** The Activity Context */
	private Context context;
	
	/**
	 * Instance the Cube object and set the Activity Context 
	 * handed over. Initiate the light buffers and set this 
	 * class as renderer for this now GLSurfaceView.
	 * Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen and Buttons  
	 * 
	 * @param context - The Activity Context
	 */
	public PA07(Context context) {
		super(context);
		game = new GameModel07(25);
		
		//Set this as Renderer
		this.setRenderer(this);
		
		//Request focus, otherwise buttons won't react
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		
		//
		this.context = context;		
		
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = byteBuf.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = byteBuf.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer1 = byteBuf.asFloatBuffer();
		lightPositionBuffer1.put(lightPosition1);
		lightPositionBuffer1.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer2 = byteBuf.asFloatBuffer();
		lightPositionBuffer2.put(lightPosition2);
		lightPositionBuffer2.position(0);
		
		//
		cube = new Cube();
		floor = new Plane();
		wallN = new Plane();
		wallS = new Plane();
		wallE = new Plane();
		wallW = new Plane();
		tprism = new TrianglePrism();
		
		/*
		try {
			suzanne = new MeshObject(context,"obj/suzanne2.obj");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("no such file 'obj/cubesds.obj' in assets");
		}
		*/
		
		
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {		
		//And there'll be light!
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT0);											//Enable Light 0 ( NEW )

		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPositionBuffer1);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT1);											//Enable Light 0 ( NEW )

		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, lightPositionBuffer2);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT2);											//Enable Light 0 ( NEW )


		//Settings
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_DITHER);				//Disable dithering ( NEW )
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0f, 0f, 1.0f, 1f); 	//White Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
				
		//Load the texture for the cube once during Surface creation
		cube.loadGLTexture(gl, this.context,R.drawable.crate);
		floor.loadGLTexture(gl, this.context, R.drawable.tiles);
		//suzanne.loadGLTexture(gl, context,R.drawable.bg);
		wallN.loadGLTexture(gl, context,R.drawable.crate);
		wallE.loadGLTexture(gl, context,R.drawable.crate);
		wallS.loadGLTexture(gl, context,R.drawable.crate);
		wallW.loadGLTexture(gl, context,R.drawable.crate);
		tprism.loadGLTexture(gl,context,R.drawable.icon);
	}
	
	/**
	 * If the surface changes, reset the viewport 
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { height = 1;} 		//Prevent A Divide By Zero By
		            						//Making Height Equal One
		
		this.width = width;    // store width/height for use by other view methods
		this.height = height;

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		
		setViewFromLeft(gl);

		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}


	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {

		//Clear Screen And Depth Buffer
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					

		
		//setViewFromAvatar(gl);
//		setViewFromBehindAvatar(gl);
		//setViewFromLeft(gl);
		setViewFromTop(gl);
		
		drawAvatar(gl);
		
		//gl.glScalef(2f, 2f, 2f);
		
		drawFloor(gl);
		drawWalls(gl);
		
		drawFoes(gl);
		
		drawProjectiles(gl);
		

		
		game.update();
		
	}	
	

	private void drawWalls(GL10 gl){
		gl.glPushMatrix();
		gl.glTranslatef(game.width/2,0f,game.height/2);
		
		gl.glPushMatrix();
		drawWall(gl,wallS);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(90, 0f, 1f, 0f);
		drawWall(gl,wallW);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(180, 0f, 1f, 0f);
		drawWall(gl,wallN);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(270, 0f, 1f, 0f);
		drawWall(gl,wallE);		
		gl.glPopMatrix();
		
		gl.glPopMatrix();
	}
	
	private void drawWall(GL10 gl,Plane wall){
		gl.glPushMatrix();
		gl.glTranslatef(-game.width/2f,0f,-game.height/2);
		gl.glRotatef(-90, 1f,0f,0f);
		gl.glScalef(game.width,1f,game.height/10);
		/*
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glRotatef(90f,0f,0f,1f);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		*/
		wall.draw(gl,filter);
		gl.glPopMatrix();
	}
	
	
	
	
	private void setViewFromAvatar(GL10 gl){
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		// Set the properties of the camera 
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		// Point and aim the camera
		float x = game.avatar.pos[0];
		float y = game.avatar.pos[1];
		float z = game.avatar.pos[2];

		float vx = game.avatar.vel[0];
		float vy = game.avatar.vel[1];
		float vz = game.avatar.vel[2];
		
		GLU.gluLookAt(gl, 
				      x,   y+2f,    z,      // eye position
				      x+vx,y+vy+2.1f, z+vz,   // target position
				      0f,  1f,      0f);    // up direction

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		
	}
	
	private void setViewFromBehindAvatar(GL10 gl){
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		// Set the properties of the camera 
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		// Point and aim the camera
		float x = game.avatar.pos[0];
		float y = game.avatar.pos[1];
		float z = game.avatar.pos[2];

		float vx = game.avatar.vel[0];
		float vy = game.avatar.vel[1];
		float vz = game.avatar.vel[2];
		
		float d = 4f/(0.01f+game.avatar.speed); // distance of camera behind avatar
		
		GLU.gluLookAt(gl, 
				      x-d*vx, y-d*vy+2f,  z-d*vz,      // eye position
				      x+vx,   y+vy+2.1f,  z+vz,   // target position
				      0f,  1f,      0f);    // up direction

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		
	}
	

	private void setViewFromTop(GL10 gl){
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		// Set the properties of the camera. we want an ortho view
//		GLU.gluOrtho2D(gl, 10f, -10f, 10f, -10f);
//		GLU.gluPerspective(gl, 90.0f, width / height, 0.1f, 10000.0f);
		gl.glOrthof(-10f, game.width + 10f, 0f, 120f, -1f, 1000f);
		gl.glTranslatef(0,20f, 0f);
		gl.glRotatef(90f, 1, 0, 0);
		gl.glTranslatef(0,-30f, -90f);
		// Point and aim the camera
//		GLU.gluLookAt(gl, 
//				game.width/2f, 60f, game.height/2f,              // eye position above Left of game board
//				     game.width/2f, 0f, game.height/2f,   // target position at center of board
//				      1f, 0f, 0f);                         // up direction

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
	}
	
	
	//this will mgive us a birds eye view
	
	private void setViewFromLeft(GL10 gl){
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		// Set the properties of the camera 
		GLU.gluPerspective(gl, 45.0f, width / height, 0.1f, 10000.0f);

		// Point and aim the camera
		GLU.gluLookAt(gl, 
				     -20f, 40f, game.width/2,              // eye position above Left of game board
				      game.width/2f, 0f, game.height/2f,   // target position at center of board
				      0f, 1f, 0f);                         // up direction

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
	}
	
	
	private void drawProjectiles(GL10 gl){
		for(Foe f:game.projectiles){
			drawProjectile(gl,f);
		}	
	}
	
	private void drawProjectile(GL10 gl, Foe f){
		gl.glPushMatrix();
		gl.glTranslatef(f.pos[0],f.pos[1],f.pos[2]);
		cube.draw(gl,filter);
		gl.glPopMatrix();
	}
	
/*
 * View Methods which use information in the model to draw the objects on the screen
 */
	private void drawFoes(GL10 gl){
		for(Foe f:game.foes){
			drawFoe(gl,f);
		}	
	}
	

	
	private void drawFoe(GL10 gl, Foe f){
		gl.glPushMatrix();
		gl.glTranslatef(f.pos[0],f.pos[1],f.pos[2]);
		//gl.glScalef(1f,2f,1f);
		gl.glRotatef((float)calcHeading(f),0f,1f,0f);
		gl.glTranslatef(0f, 2f, 0f);
		gl.glPushMatrix();
		gl.glTranslatef(-2f,0f,1f);
		gl.glScalef(4f,4f,4f);
		gl.glRotatef(45f, 0f, 1f, 0f);
		//suzanne.draw(gl);
		tprism.draw(gl, filter);
		gl.glPopMatrix();
		//cube.draw(gl, filter);
		gl.glTranslatef(0.5f,1.0f,0.5f);
		drawArm(gl,f);
		gl.glRotatef(180f,0f,1f,0f);
		drawArm(gl,f);
		//suzanne.draw(gl);
		//System.out.println("drawing foe:"+f.pos[0]+" "+f.pos[2]);
		gl.glPopMatrix();
	}
	

	private void drawArm(GL10 gl, Foe f){
		gl.glPushMatrix();
		gl.glTranslatef(0.5f,0f,0f);
		gl.glRotatef(20f*(float)Math.sin(f.armAngle*0.05),0f,0f,1f);
		drawBox(gl,2f,1f,1f);
		gl.glTranslatef(2f, 0f, 0f);
		gl.glRotatef(20f*(float)(Math.sin(f.armAngle*0.05)),0f,0f,1f);
		drawBox(gl,2f,1f,1f);
		gl.glTranslatef(2f, 0f, 0f);
		gl.glRotatef(20f*(float)(Math.sin(f.armAngle*0.05)),0f,0f,1f);
		drawBox(gl,2f,1f,1f);
		gl.glPopMatrix();
		
	}
	
	private void drawBox(GL10 gl, float x, float y, float z){
		gl.glPushMatrix();
		gl.glScalef(x,y,z);
		cube.draw(gl,filter);
		gl.glPopMatrix();
	}
	
	private double calcHeading(Foe f){
		float x = f.vel[0];
		float z = f.vel[2];
		double heading = Math.atan2(x,z)/Math.PI*180;
		return heading;
	}
	
	private void drawAvatar(GL10 gl){
		
		gl.glPushMatrix();
		gl.glTranslatef(game.avatar.pos[0],0f,game.avatar.pos[2]);
		//gl.glScalef(1f,1.5f,1f);
		gl.glRotatef((float)calcHeading(game.avatar),0f,1f,0f);
		//suzanne.draw(gl);
		this.tprism.draw(gl,filter);
		//cube.draw(gl, filter);
		//System.out.println("drawing avatar:"+game.avatar.pos[0]+" "+game.avatar.pos[2]);
		gl.glPopMatrix();
	}
	
	

	private void drawFloor(GL10 gl){
		gl.glPushMatrix();
		gl.glScalef(game.width,1f,game.height);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glScalef(8f,1f,0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		floor.draw(gl,filter);
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
	}


	
	
	
	
	
	
	long lastEvent=System.currentTimeMillis();
	long clickStart = 0;
	int clickCount=0;
	
/* ***** Listener Events ( NEW ) ***** */		
	/**
	 * Override the touch screen listener.
	 * 
	 * React to moves and presses on the touchscreen.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//
		float x = event.getX();
        float y = event.getY();
        long currentTime = System.currentTimeMillis();
        //System.out.println("ME="+event);
        
        //If a touch is moved on the screen
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
        	controlMouseDrag(x,y);
        //A press on the screen
        } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
        	clickStart = currentTime;
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
        	if (currentTime-clickStart<250L){
        		clickCount++;
        		controlMouseClick(x,y);
        		System.out.println("mouse click!");
        	} 
        }
        
        //Remember the values
        oldX = x;
        oldY = y;
        
        //We handled the event
		return true;
	}

	private void controlMouseClick(float x,float y) {
		// create an box and throw it!
		this.game.readyToFire = true; // fireProjectile();
		System.out.println("firing projectile");
	}

	private void controlMouseDrag(float x, float y) {
		//Calculate the change
		float dx = x - oldX;
		float dy = y - oldY;

		// Calculate the horizontal and vertical offset since last drag event
		float xoffset =  dx; //(x-width/2)/(width/2);
		float yoffset = -dy; //(height/2-y)/(height/2);

		
		// update heading using xoffset
		float dheading = xoffset*2;
		game.avatar.heading += dheading;
		
		// update speed using yoffset
		float dspeed = game.avatar.speed + yoffset*0.01f;
		if (dspeed<0.05) dspeed = 0.05f;
		else if (dspeed > 5) dspeed = 5f;
		game.avatar.speed = dspeed; 

	}
	
	
}
