package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.*;
import java.util.*;

public class KittensGame extends ApplicationAdapter {
	SpriteBatch batch;
	Music bgMusic;
	public static final int LEFT=0;
	public static final int RIGHT=1;
	public static final int UP=2;
	public static final int DOWN=3;
	private static String mode="side";
	private Kitten kat;
	Pixmap forestMap;
	int forestWall;
	Texture forestBG;
	public void modeShift(){
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
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
		private int mapx,mapy,sidex,sidey,health,mapCurAction,sideCurAction;
		private boolean alive,attacking;
		private Texture [][] mapframes= new Texture[1][7];
		//Left,Right,Up,Down
		//Animation frames
		private Texture [][] sideframes= new Texture[2][7];
		//Left,Right,AtkLeft,AtkRight,ult
		//Animation frames
		private int curFrame,curAtkFrame;
		double count;	
		
		public Kitten(int xx,int yy,Texture[][]mapframelist,Texture[][]sideframelist){
			mapx=xx;
			mapy=yy;
			sidex=xx;
			sidey=yy;
			health=10;
			mapframes=mapframelist;
			sideframes=sideframelist;
			alive = true;
			curFrame=0;
			curAtkFrame=0;
			mapCurAction=0;
			sideCurAction=0;
			count = 0.0;
			attacking=false;
		}
		public void idleDraw(){
			if (mode.equals("map")){
				batch.draw(mapframes[mapCurAction][curFrame],mapx,mapy);
			}
			if (mode.equals("side")){
				batch.draw(sideframes[sideCurAction][0],sidex,sidey);
			}
		}
		public void draw(){
			if (mode.equals("map")){
				batch.draw(mapframes[mapCurAction][curFrame],mapx,mapy);
			}
			System.out.println(curFrame);
			System.out.println(mapCurAction);
			if (mode.equals("side")){
				batch.draw(sideframes[sideCurAction][curFrame],sidex,sidey);
			}		
		}
		public void attackAndDraw(){
			if(Gdx.input.isKeyPressed(Keys.X) || attacking){
				if (mode.equals("side")){
					attacking=true;
					batch.draw(sideframes[sideCurAction+2][curAtkFrame],mapx,mapy);
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
		public void moveAndDraw(){
			boolean drawn=false;
			if (alive && !attacking){
				if (mode.equals("side")){
					if(sidey>170){
						sidey-=10;
					}
				}
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
							sidex-=5;
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
							sidex+=5;
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
				}
				//if(Gdx.input.isKeyJustPressed(Keys.W)||Gdx.input.isKeyJustPressed(Keys.UP)){
				if(Gdx.input.isKeyPressed(Keys.W)||Gdx.input.isKeyPressed(Keys.UP)){
					if (mode.equals("side")){
						if ((sidey+150)<=900){
							sidey+=20;
						}
						drawn=true;
					}
				}
				else if(Gdx.input.isKeyPressed(Keys.S)||Gdx.input.isKeyPressed(Keys.DOWN)){
					if (mode.equals("map")){
						if(mapy<0){
							mapy-=10;
						}
						mapCurAction=DOWN;
						drawn=true;
					}
				}
				if (drawn){
					draw();
				}
				else{
					idleDraw();
				}
				count += .0625;
				curFrame=(int)(count)%7;
			}
		}
		public int getMapX(){return mapx;}
		public int getMapY(){return mapy;}
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
		batch = new SpriteBatch();
		forestWall = cNum(0,0,255,255);
		Texture[][]mapFrames=new Texture[1][7];
		Texture[][]sideFrames=new Texture[2][7];
		/*for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				mapFrames[i][j]=new Texture("mapFrames"+(i+1)+"-"+(j+1)+".png");
			}
		}
		for(int i=0;i<5;i++){
			for(int j=0;j<4;j++){
				sideFrames[i][j]=new Texture("sideFrames"+(i+1)+"-"+(j+1)+".png");
			}
		}*/
		for(int i=0;i<1;i++){
			for(int j=0;j<7;j++){
				mapFrames[i][j]=new Texture("sideFrames"+(i+1)+"-"+(j+1)+".png");
			}
		}
		for(int i=0;i<2;i++){
			for(int j=0;j<7;j++){
				sideFrames[i][j]=new Texture("sideFrames"+(i+1)+"-"+(j+1)+".png");
			}
		}
		forestBG = new Texture("Forest1.png");
		kat= new Kitten(10,170,mapFrames,sideFrames);
	}

	@Override
	public void render () {
		//Always stuff and then drawing
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		drawBack();
		kat.moveAndDraw();
		kat.attackAndDraw();
		batch.end();
	}
}
