package com.linhpriyonto.game;
//DISCLAIMMER: Don't play for 20 years :O
//save progress with text file

import java.util.ArrayList;
import java.util.Random;
import java.math.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class KittensGame extends ApplicationAdapter {
	Music bgMusic;
	SpriteBatch batch;
	Texture img,menu,forestBG;
	Pixmap forestMap;
	int x=10,y=176,direct=1,forestWall;
	boolean still = true;
	
	private ArrayList<Texture[]>noLegsMove = new ArrayList<Texture[]>();
	
	//private Texture[]menuMoves = new Texture[7];
	
	private Texture[]nolegsStandL = new Texture [7];
	private Texture[]nolegsStandR = new Texture [7];
	private Texture[]nolegsRunL = new Texture [7];
	private Texture[]nolegsRunR = new Texture [7];
	private Texture[]nolegsJumpL = new Texture [7];
	private Texture[]nolegsJumpR = new Texture [7];
	//private Texture[]nolegsLandR = new Texture [7];
	//private Texture[]nolegsLandR = new Texture [7];
	
	
	public static final int LEFT=0, RIGHT=1, UP=2, DOWN=3;
	private static String mode = "map";
	
	
	CatChar noLegs;
	
	//MenuScreen mainMenu,inScreen;

	public void modeShift(){
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			mode = mode.contentEquals("map")?"side":"map";
		}
	}
	public int cNum(int r,int g,int b, int a){
		return (r<<24) + (g<<16) + (b<<8) + a;
	}
	
	@Override
	public void create () {
		//bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Unity.mp3"));
		//bgMusic.play(); 
		
		batch = new SpriteBatch();
		
		//mainMenu = new MenuScreen(0,menuMoves,)
		
		
		//BOUNDARIES
		//r,g,b,a
		forestWall = cNum(255,0,0,255);
		
		//MASKS
		forestMap = new Pixmap(Gdx.files.internal("ForestMask.png"));
		
		//Facing Left
		for (int i=0;i<nolegsStandL.length;i++){
			String image = "StandingNoLegs"+(i+8)+".png";
			nolegsStandL [i] = new Texture(image);
		}
		noLegsMove.add(nolegsStandL);
		//Facing Right
		for (int i=0;i<nolegsStandR.length;i++){
			String image = "StandingNoLegs"+(i+1)+".png";
			nolegsStandR [i] = new Texture(image);
		}
		noLegsMove.add(nolegsStandR);
		
		//Running Left
		for (int i=0;i<nolegsRunL.length;i++){
			String image = "RunningNoLegs"+(i+8)+".png";
			nolegsRunL [i] = new Texture(image);
		}
		noLegsMove.add(nolegsRunL);
		//Running Right
		for (int i=0;i<nolegsRunR.length;i++){
			String image = "RunningNoLegs"+(i+1)+".png";
			nolegsRunR [i] = new Texture(image);
		}
		noLegsMove.add(nolegsRunR);
		
		
		noLegs = new CatChar(x,y,nolegsStandR,nolegsStandR.length);
		//menu = new Texture("menu.jpg");
		forestBG = new Texture("Forest1.png");
	}
	public boolean checkClear(int x,int y){
		if(x<0 || x>= forestMap.getWidth() || y<0 || y>= forestMap.getHeight()){
			return false;
		}
		int c = forestMap.getPixel(x, forestMap.getHeight()- y);
		return c != forestWall;
	}

	@Override
	public void render () {
		//Left
		if (Gdx.input.isKeyPressed(Keys.LEFT) && checkClear(x-10,y)){	
			if ((x-10)>=0){
				x-=5;
			}
			still = false;
			direct=0;
		}
		//Right
		//if the user clicks the right arrow key, the user moves right 5 units
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) && checkClear(x+130,y)){
			if ((x+130)<=1456){
				x+=5;
			}
			still = false;
			direct=1;
		}//Up (Jump)
		if (Gdx.input.isKeyPressed(Keys.UP) && checkClear(x,y+150)){
			if ((y+150)<=900){
				y+=5;
			}
			still = false;
			direct=2;
		}
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//batch.draw(menu, 0, 0);
		batch.draw(forestBG,0,0);
		noLegs.move();
		//System.out.println(noLegs.getFrame());
		if (direct==LEFT){
			if (still==true){
				batch.draw(noLegsMove.get(0)[noLegs.getFrame()],x,y);
			}
			else{
				batch.draw(noLegsMove.get(2)[noLegs.getFrame()],x,y);
			}
			//play scurry sound effect
		}
		else if(direct==RIGHT){
			if (still==true){
				batch.draw(noLegsMove.get(1)[noLegs.getFrame()],x,y);
			}
			else{
				batch.draw(noLegsMove.get(3)[noLegs.getFrame()],x,y);
			}
			
			//play scurry sound effect
		}
		if (direct==UP){
			//batch.draw the jump
			//batch.draw the landing
			//play bounce
		}
		still = true;
		batch.end();
	}
}
class CatChar{
	private int mapx,mapy,sidex,sidey,size,health,mapCurAction,sideCurAction;
	private double curFrame;
	private Texture[]frames;
	private boolean alive,attacking;
	private Texture[][] mapframes = new Texture [4][4];
	private Texture[][] sideframes = new Texture[5][4];
	
	public CatChar(int sidex, int sidey,Texture[]framelist,int size){
		this.sidex=sidex;
		this.sidey=sidey;
		frames = framelist;
		this.size=size;
		alive=true;
		curFrame=0.0;
	}
	public void move(){
		curFrame+=.0625;
	}
	public void takeLife(){
		if (health>0){
			health-=1;
		}
		else{
			health=0;
		}
	}
	public void gainLife(){
		health+=1;
	}
	public int getLifeNum(){
		return health;
	}
	public int getFrame(){
		return (int)(curFrame)%size;
	}
	public boolean getAlive(){
		return alive;
	}
	public void setAlive(boolean statement){
		alive=statement;
	}
	public int getX(){
		return sidex;
	}
	public int getY(){
		return sidey;
	}
}

class Enemy{
	private int ex,ey,size;
	private double curFrame;
	private Texture[]frames;
	private boolean alive;
	
	public Enemy(int ex, int ey,Texture[]framelist,int size){
		this.ex=ex;
		this.ey=ey;
		frames = framelist;
		this.size=size;
		alive=true;
		curFrame=0.0;
	}
	public void move(){
		curFrame+=.0625;
	}
	public void fall(){
		//gravity
	}
	public int getFrame(){
		return (int)(curFrame)%size;
	}
	public int geteX(){
		return ex;
	}
	public int geteY(){
		return ey;
	}
}

/*
Pixmap mask

mask = new Pixmas("    ");
int col = mask.getPixel(x,y);

//RGBA form

*/