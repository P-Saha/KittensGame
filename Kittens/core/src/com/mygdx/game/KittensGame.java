package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
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
	private ArrayList<Bullet>bullets = new ArrayList<Bullet>();
	public static final int LEFT=0;
	public static final int RIGHT=1;
	public static final int UP=2;
	public static final int DOWN=3;
	OrthographicCamera camera;
	private static String mode="side";
	int jumpAt=15;
	private Kitten kat;
	private SideEnemy enemy;
	Level curLevel;
	Level [] levelist;
	Pixmap forestMap;
	int forestWall;
	Texture forestBG,bulletPic;
	boolean jumped=false;
	boolean wUp=false;
	//170 y is ground for now
	public void modeShift(){
		if(Gdx.input.isKeyJustPressed(Keys.X)){
			mode=mode.equals("map")?"side":"map";
		}
		if (mode.equals("map")){
			for(Level level:levelist){
				if(kat.getHitbox().overlaps(level.getHitbox()) && !level.isCompleted()){
					curLevel=level;
					mode="side";
				}
			}
		}
		if (mode.equals("side")){
			if(kat.getSideX()>curLevel.getMaxX()||curLevel.genocideCheck()){
				curLevel.setCompleted(true);
				mode="map";
			}
		}
	}
	public void drawBack(){
		if (mode.equals("map")){
			batch.draw(forestBG,0,0);
		}
		if (mode.equals("side")){
			batch.draw(forestBG,0,0);
		}
	}
	public int cNum(int r,int g,int b, int a){
		return (r<<24) + (g<<16) + (b<<8) + a;
	}
	class Kitten{
		private int mapx,mapy,sidex,sidey,vy,health,mapCurAction,sideCurAction,oldCurAction,jumpDirect;
		private boolean alive,attacking,drawn;
		private Texture [][] mapFrames= new Texture[4][7];
		//Left,Right,Up,Down
		//Animation frames
		private Texture [][] sideFrames= new Texture[4][7]; 
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
			jumpDirect=1;
			count = 0.0;
			attacking=false;
			drawn=false;
			hitbox=new Rectangle(0,0,0,0);
		}
		public void collide(SideEnemy enemy){
			if (hitbox.overlaps(enemy.getHitbox())){
			}
		}
		public void idleDraw(){
			if (mode.equals("map")){
				batch.draw(mapFrames[mapCurAction][curFrame],mapx,mapy);
				hitbox.set(mapx,mapy,mapFrames[mapCurAction][curFrame].getWidth(),mapFrames[mapCurAction][curFrame].getHeight());
			}
			if (mode.equals("side")){
				if (sideCurAction<2){
					batch.draw(sideIdleFrames[sideCurAction][curFrame],sidex,sidey);
					hitbox.set(sidex,sidey,sideIdleFrames[sideCurAction][curFrame].getWidth(),sideIdleFrames[sideCurAction][curFrame].getHeight());
				}
				else{
					batch.draw(sideFrames[sideCurAction][curFrame],sidex,sidey);
					hitbox.set(sidex,sidey,sideFrames[sideCurAction][curFrame].getWidth(),sideFrames[sideCurAction][curFrame].getHeight());
				}
				sideCurAction=oldCurAction;
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
			if(Gdx.input.isKeyPressed(Keys.SPACE)||attacking){
				if (mode.equals("side")){
					curAtkFrame++;
					attacking=true;
					//batch.draw(sideFrames[sideCurAction+2][curAtkFrame],mapx,mapy);
					if (curAtkFrame==2){
						Bullet katBullet = new Bullet(sidex,sidey,bulletPic,jumpDirect);
						bullets.add(katBullet);
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
						oldCurAction=LEFT;
						jumpDirect=0;
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
						oldCurAction=RIGHT;
						jumpDirect=1;
						drawn=true;
					}
				}
				if(Gdx.input.isKeyPressed(Keys.W)||Gdx.input.isKeyPressed(Keys.UP)){
					if (mode.equals("map")){
						if(mapy<1024-200){
							mapy+=10;
						}
						//mapCurAction=UP;
						drawn=true;
					}
					if (mode.equals("side")){
						if(sidey<=190){
							vy=20;
							jumpAt=sidey;
						}
					}
					
				}
				if(Gdx.input.isKeyPressed(Keys.S)||Gdx.input.isKeyPressed(Keys.DOWN)){
					if (mode.equals("map")){
						if(mapy<1000){
							mapy-=10;
						}
						//mapCurAction=DOWN;
						drawn=true;
					}
				}
				if (mode.equals("side")){
					vy-=1;
					if(170<sidey+vy){
						sidey+=vy;
						sideCurAction=UP+jumpDirect;
					}
					else{
						sidey=170;
						vy=0;
					}
				}
				//System.out.println(vy);
				//System.out.println(sidey);
				/*if(Gdx.input.isKeyPressed(Keys.SPACE)){
					if(mode.equals("side")){
						Bullet katBullet = new Bullet(sidex,sidey,bulletPic,jumpDirect);
						bullets.add(katBullet);
						curAtkFrame++;
					}
				}*/
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
		public boolean isAlive(){return alive;}
		public Rectangle getHitbox(){return hitbox;}
	}
	public boolean checkClear(int x,int y){
		if(x<0 || x>= forestMap.getWidth() || y<0 || y>= forestMap.getHeight()){
			return false;
		}
		int c = forestMap.getPixel(x, forestMap.getHeight()- y);
		return c != forestWall;
	}
	public void moveBullet(){
		batch.begin();
		if (mode.equals("side")){
			if (bullets.size()>0){
				for (int i=0;i<bullets.size();i++){
					Bullet bull = bullets.get(i);
					batch.draw(bulletPic,bull.bx,bull.by);
					if (bull.getBullDirect()==LEFT){
						bull.bulletLeft();
						if (bull.bx<=0){
							bullets.remove(bull);
						}
					}
					else if (bull.getBullDirect()==RIGHT){
						bull.bulletRight();
						if (bull.bx>=1456){
							bullets.remove(bull);
						}
					}
				}
			}
		}
		batch.end();
	}
	class Level{
		private SideEnemy [] enemies;
		private Texture background,icon;
		private boolean completed;
		private int x,y,maxX;
		private Rectangle hitbox;
		public Level(SideEnemy[] enemylist, Texture backgroundpic, int xx, int yy){
			enemies=enemylist;
			background=backgroundpic;
			completed=false;
			x=xx;
			y=yy;
			hitbox=new Rectangle(xx,yy,icon.getWidth(),icon.getHeight());
			maxX=background.getWidth()-16;
		}
		public boolean genocideCheck(){
			for (SideEnemy enemy:enemies){
				if (enemy.isAlive()==true){
					return false;
				}
			}
			return true;
		}
		public SideEnemy[] getEnemies(){return enemies;}
		public Texture getBackground(){return background;}
		public boolean isCompleted(){return completed;}
		public void setCompleted(boolean val){completed=val;}
		public Rectangle getHitbox(){return hitbox;}
		public int getMaxX(){return maxX;}
		
	}
	class Bullet{
		int bx,by,direct;
		Texture bulletPic;
		public Bullet (int kx, int ky,Texture bulletPic,int direction){
			bx = kx;
			by = ky;
			this.bulletPic = bulletPic;
			direct = direction;
		}
		public void bulletLeft(){
			bx-=5;
		}
		public void bulletRight(){
			bx+=5;
		}
		public int getBullDirect(){
			return direct;
		}
	}
	@Override
	public void create () {
		DisplayMode dm=Gdx.graphics.getDesktopDisplayMode();
		Gdx.graphics.setDisplayMode(dm.width,dm.height,false);
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
		for(int i=0;i<7;i++){
			sideIdleFrames[0][i]=new Texture("sideFrames1-"+(i+1)+".png");
			sideIdleFrames[1][i]=new Texture("sideFrames1-"+(i+8)+".png");
		}
		for(int i=0;i<2;i++){
			for(int j=0;j<7;j++){
				sideFrames[i][j]=new Texture("sideFrames"+(i+2)+"-"+(j+1)+".png");
				sideFrames[i+2][j]=new Texture("sideFrames"+(i+7)+"-"+(j+1)+".png");
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
		bulletPic=new Texture("Bullet.png");
		kat=new Kitten(10,170,mapFrames,sideFrames,sideIdleFrames);
		enemy=new SideEnemy(10,170,sideIdleFrames,3);
	}
	private void updateCamera(){
		float camX=Math.max(Gdx.graphics.getWidth()/2+6, Math.min(forestBG.getWidth()-Gdx.graphics.getWidth()/2-6, kat.getSideX()));
		float camY=Math.max(Gdx.graphics.getHeight()/2+32, Math.min(forestBG.getHeight()-Gdx.graphics.getHeight()/2-20, kat.getSideY()));
		if (mode.equals("map")){
			camX=Math.max(Gdx.graphics.getWidth()/2+6, Math.min(forestBG.getWidth()-Gdx.graphics.getWidth()/2-6, kat.getMapX()));
			camY=Math.max(Gdx.graphics.getHeight()/2+32, Math.min(forestBG.getHeight()-Gdx.graphics.getHeight()/2-32, kat.getMapY()));
		}
		//jan helped me here
		camera.position.set(camX,camY,0);
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
		moveBullet();
		kat.collide(enemy);
	}
}
