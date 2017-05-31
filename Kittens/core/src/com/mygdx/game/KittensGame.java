package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.io.*;
import java.util.*;

public class KittensGame extends ApplicationAdapter{
	SpriteBatch batch;
	Music bgMusic;
	public static final int LEFT=0;
	public static final int RIGHT=1;
	public static final int UP=2;
	public static final int DOWN=3;
	OrthographicCamera camera;
	private static String mode="side";
	int jumpAt=15;
	private Kitten kat;
	private SideEnemy enemy;
	Pixmap forestMap;
	int forestWall;
	Texture forestBG;
	boolean jumped=false;
	boolean wUp=false;
	//170 y is ground for now
	public void modeShift(){
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			mode=mode.equals("map")?"side":"map";
		}
	}
	public void drawBack(){
		if (mode.equals("map")){
			//////////////batch.draw(,kat.getMapX(),kat.getMapY());
		}
		if (mode.equals("side")){
			batch.draw(forestBG,0,0);
		}
	}
	public int cNum(int r,int g,int b, int a){
		return (r<<24) + (g<<16) + (b<<8) + a;
	}
	class Kitten{
		private int mapx,mapy,sidex,sidey,vy,health,mapCurAction,sideCurAction;
		private boolean alive,attacking,drawn;
		private Texture [][] mapFrames= new Texture[4][7];
		//Left,Right,Up,Down
		//Animation frames
		private Texture [][] sideFrames= new Texture[7][7]; 
		//RunLeft,RunRight,AtkLeft,AtkRight,JumpLeft,JumpRight,ult, I HATE JUMPING
		//Animation frames
		private Texture [][] sideIdleFrames=new Texture[2][7];
		//Left,Right
		private int curFrame,curAtkFrame;
		private double count;
		private Rectangle hitbox;
		
		public Kitten(int xx,int yy,Texture[][]mapframelist,Texture[][]sideframelist,Texture[][]sideidleframelist){
			mapx=xx;
			mapy=yy;
			sidex=xx;
			sidey=yy;
			vy=0;
			health=10;
			mapFrames=mapframelist;
			sideFrames=sideframelist;
			sideIdleFrames=sideidleframelist;
			alive = true;
			curFrame=0;
			curAtkFrame=0;
			mapCurAction=0;
			sideCurAction=0;
			count = 0.0;
			attacking=false;
			drawn=false;
			hitbox=new Rectangle(0,0,0,0);
		}
		public void collide(SideEnemy enemy){
			if (hitbox.overlaps(enemy.getHitbox())){
				//System.out.println("hit");
			}
		}
		public void idleDraw(){
			if (mode.equals("map")){
				batch.draw(mapFrames[mapCurAction][curFrame],mapx,mapy);
				hitbox.set(mapx,mapy,mapFrames[mapCurAction][curFrame].getWidth(),mapFrames[mapCurAction][curFrame].getHeight());
			}
			if (mode.equals("side")){
				batch.draw(sideIdleFrames[sideCurAction][curFrame],sidex,sidey);
				hitbox.set(sidex,sidey,sideIdleFrames[sideCurAction][curFrame].getWidth(),sideIdleFrames[sideCurAction][curFrame].getHeight());
			}
		}
		public void draw(){
			if (drawn){
				if (mode.equals("map")){
					batch.draw(mapFrames[mapCurAction][curFrame],mapx,mapy);
					hitbox.set(mapx,mapy,mapFrames[mapCurAction][curFrame].getWidth(),mapFrames[mapCurAction][curFrame].getHeight());
				}
				//System.out.println(curFrame);
				//System.out.println(mapCurAction);
				if (mode.equals("side")){
					batch.draw(sideFrames[sideCurAction][curFrame],sidex,sidey);
					hitbox.set(sidex,sidey,sideFrames[sideCurAction][curFrame].getWidth(),sideFrames[sideCurAction][curFrame].getHeight());
				}
			}
			else{
				idleDraw();
			}
		}
		public void attackAndDraw(){
			if(Gdx.input.isKeyPressed(Keys.X) || attacking){
				if (mode.equals("side")){
					attacking=true;
					batch.draw(sideFrames[sideCurAction+2][curAtkFrame],mapx,mapy);
					curAtkFrame++;
					if (curAtkFrame>2 /*and collides with enemy*/){////////////////////////////////////////
						//deal damage//////////////////////////////////////////////////
					}
					if (curAtkFrame>5){///////////////////////////////////////////////////////
						attacking=false;
						curAtkFrame=0;
					}
				}
			}
		}
		public void move(){
			drawn=false;
			if (alive && !attacking){
				if(Gdx.input.isKeyPressed(Keys.A)||Gdx.input.isKeyPressed(Keys.LEFT)){
					if (mode.equals("map")){
						if(mapx>0){
							mapx-=10;
						}
						mapCurAction=LEFT;
						drawn=true;
					}
					if (mode.equals("side")){
						if ((sidex-10)>=0){
							sidex-=10;
						}
						sideCurAction=LEFT;
						drawn=true;
					}
				}
				if(Gdx.input.isKeyPressed(Keys.D)||Gdx.input.isKeyPressed(Keys.RIGHT)){
					if (mode.equals("map")){
						if(mapx<2048-500){
							mapx+=10;
						}
						mapCurAction=RIGHT;
						drawn=true;
					}
					if (mode.equals("side")){
						if ((sidex+130)<=1456){
							sidex+=10;
						}
						sideCurAction=RIGHT;
						drawn=true;
					}
				}
				if(Gdx.input.isKeyPressed(Keys.W)||Gdx.input.isKeyPressed(Keys.UP)){
					if (mode.equals("map")){
						if(mapy<1024-200){
							mapy+=10;
						}
						mapCurAction=UP;
						drawn=true;
					}
					if (mode.equals("side")){
						if(sidey<=190){
							vy=20;
							jumpAt=sidey;
						}
					}
					
				}
				if (mode.equals("side")){
					vy-=1;
					if(170<sidey+vy){
						sidey+=vy;
					}
					else{
						sidey=170;
						vy=0;
					}
				}
				//System.out.println(vy);
				//System.out.println(sidey);
				if(Gdx.input.isKeyPressed(Keys.S)||Gdx.input.isKeyPressed(Keys.DOWN)){
					if (mode.equals("map")){
						if(mapy<1000){
							mapy-=10;
						}
						mapCurAction=DOWN;
						drawn=true;
					}
				}
				count += .0625;
				curFrame=(int)(count)%7;
			}
		}
		public int getMapX(){return mapx;}
		public int getMapY(){return mapy;}
		public int getSideX(){return sidex;}
		public int getSideY(){return sidey;}
		public Rectangle getHitbox(){return hitbox;}
	}
	class SideEnemy{
		private int x,y,health,curFrame,curAction,speed;
		private double count;
		private Texture[][]frames;
		private boolean alive;
		private Rectangle hitbox;
		private SideEnemy(int xx,int yy,Texture[][]framelist, int spd){
			x=xx;
			y=yy;
			health=10;
			frames=framelist;
			curFrame=0;
			curAction=RIGHT;
			count = 0.0;
			alive=true;
			speed=spd;
			hitbox=new Rectangle(0,0,0,0);
		}
		public void move(){
			if (alive && mode.equals("side")){
				if(kat.getSideX()<x){
					curAction=LEFT;
				}
				else{
					curAction=RIGHT;
				}
				if(curAction==RIGHT){
					x+=speed;
				}
				if(curAction==LEFT){
					x-=speed;
				}
				count += .0625;
				curFrame=(int)(count)%7;
			}
		}
		public void draw(){
			if (mode.equals("side")){
				batch.draw(frames[curAction][curFrame],x,y);
				hitbox.set(x,y,frames[curAction][curFrame].getWidth(),frames[curAction][curFrame].getHeight());
			}	
		}
		public int getX(){return x;}
		public int getY(){return y;}
		public Rectangle getHitbox(){return hitbox;}
	}
	public boolean checkClear(int x,int y){
		if(x<0 || x>= forestMap.getWidth() || y<0 || y>= forestMap.getHeight()){
			return false;
		}
		int c = forestMap.getPixel(x, forestMap.getHeight()- y);
		return c != forestWall;
	}
	@Override
	public void create () {
		DisplayMode dm=Gdx.graphics.getDesktopDisplayMode();
		////INSRERT LINE HEAR ABOUT DISPLAY
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		forestWall = cNum(0,0,255,255);
		Texture[][]mapFrames=new Texture[4][7];
		Texture[][]sideFrames=new Texture[7][7];
		Texture[][]sideIdleFrames=new Texture[2][7];
		/*for(int i=0;i<4;i++){
			for(int j=0;j<7;j++){
				mapFrames[i][j]=new Texture("mapFrames"+(i+2)+"-"+(j+1)+".png");
			}
		}
		for(int i=0;i<5;i++){
			for(int j=0;j<7;j++){
				sideFrames[i][j]=new Texture("sideFrames"+(i+2)+"-"+(j+1)+".png");
			}
		}
		for(int i=0;i<14;i++){
			sideIdleFrames[i/7][i%7+1]=new Texture("sideFrames1-"+(i+1)+".png");
		} 
		*/ 
		for(int i=0;i<14;i++){
			sideIdleFrames[i/7][i%7]=new Texture("sideFrames1-"+(i+1)+".png");
		}
		for(int i=0;i<2;i++){
			for(int j=0;j<7;j++){
				sideFrames[i][j]=new Texture("sideFrames"+(i+2)+"-"+(j+1)+".png");
			}
		}
		for(int i=0;i<4;i++){
			for(int j=0;j<7;j++){
				mapFrames[i][j]=new Texture("sideFrames"+(i%2+2)+"-"+(j+1)+".png");
			}
		}
		/*for(int i=0;i<2;i++){
			for(int j=0;j<7;j++){
				sideIdleFrames[i][j]=new Texture("sideFrames"+(i+1)+"-"+(j+1)+".png");
			}
		} 
		for(int i=0;i<4;i++){
			for(int j=0;j<7;j++){
				mapFrames[i][j]=new Texture("sideFrames"+(i%2+1)+"-"+(j+1)+".png");
			}
		}
		for(int i=0;i<2;i++){
			for(int j=0;j<7;j++){
				sideFrames[i][j]=new Texture("sideFrames"+(i+1)+"-"+(j+1)+".png");
			}
		}*/
		forestBG = new Texture("Forest1.png");
		kat=new Kitten(10,170,mapFrames,sideFrames,sideIdleFrames);
		enemy=new SideEnemy(10,170,sideIdleFrames,3);
	}
	private void updateCamera(){
		if (mode.equals("map")){
			camera.position.set(kat.getMapX()+140/2,kat.getMapY()+120/2,0);
		}
		if (mode.equals("side")){
			if(kat.getSideX()>Gdx.graphics.getWidth()/2){
				camera.position.set(kat.getSideX(),kat.getSideY()+170,0);
			}
			else{
				camera.position.set(Gdx.graphics.getWidth()/2,kat.getSideY()+170,0);
			}
			//System.out.println(kat.getSideX());
			//System.out.println(kat.getSideY());
		}
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	@Override
	public void render() {
		//Always stuff and then drawing
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		modeShift();
		updateCamera();
		kat.move();
		enemy.move();
		batch.begin();
		drawBack();
		kat.draw();
		kat.attackAndDraw();
		enemy.draw();
		batch.end();
		kat.collide(enemy);
	}
}
