package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.*;
import java.util.*;

public class KittensGame extends ApplicationAdapter {
	SpriteBatch batch;
	public static final int LEFT=0;
	public static final int RIGHT=1;
	public static final int UP=2;
	public static final int DOWN=3;
	private static String mode="map";
	private Kitten kat;
	public void modeShift(){
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			mode=mode.equals("map")?"side":"map";
		}
	}
	public void drawBack(){
		if (mode.equals("map")){
			//////////////batch.draw(,kat.getMapX(),kat.getMapY());
		}
	}
	class Kitten{
		private int mapx,mapy,sidex,sidey,health,mapCurAction,sideCurAction;
		private boolean alive,attacking;
		private Texture [][] mapframes= new Texture[4][4];
		//Left,Right,Up,Down
		//Animation frames
		private Texture [][] sideframes= new Texture[5][4];
		//Left,Right,AtkLeft,AtkRight,ult
		//Animation frames
		private int curFrame,curAtkFrame;
		double count;	
		
		public Kitten(int xx,int yy,Texture[][]mapframelist,Texture[][]sideframelist){
			mapx=xx;
			mapy=yy;
			sidex=50;
			sidey=50;
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
				batch.draw(sideframes[sideCurAction][0],mapx,mapy);
			}
		}
		public void draw(){
			if (mode.equals("map")){
				batch.draw(mapframes[mapCurAction][curFrame],mapx,mapy);
			}
			if (mode.equals("side")){
				batch.draw(sideframes[sideCurAction][curFrame],mapx,mapy);
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
			if (alive && !attacking){
				if (mode.equals("side")){
					if(sidey>20){
						sidey-=5;
					}
				}
				if(Gdx.input.isKeyPressed(Keys.A)||Gdx.input.isKeyPressed(Keys.LEFT)){
					if (mode.equals("map")){
						if(mapx>0){
							mapx-=10;
						}
						mapCurAction=LEFT;
						draw();
					}
					if (mode.equals("side")){
						if(sidex>0){
							sidex+=10;
						}
						sideCurAction=LEFT;
						draw();
					}
				}
				if(Gdx.input.isKeyPressed(Keys.D)||Gdx.input.isKeyPressed(Keys.RIGHT)){
					if (mode.equals("map")){
						if(mapx<2048-500){
							mapx+=10;
						}
						mapCurAction=RIGHT;
						draw();
					}
					if (mode.equals("side")){
						if((sidex+150)<=900){
							sidex+=5;
						}
						sideCurAction=RIGHT;
						draw();
					}
				}
				if(Gdx.input.isKeyPressed(Keys.W)||Gdx.input.isKeyPressed(Keys.UP)){
					if (mode.equals("map")){
						if(mapy<1024-200){
							mapy+=10;
						}
						mapCurAction=UP;
						draw();
					}
				}
				if(Gdx.input.isKeyJustPressed(Keys.W)||Gdx.input.isKeyJustPressed(Keys.UP)){
					if (mode.equals("side")){
						if(sidey<1024-200){
							sidey+=20;
						}
						draw();
					}
				}
				if(Gdx.input.isKeyPressed(Keys.S)||Gdx.input.isKeyPressed(Keys.DOWN)){
					if (mode.equals("map")){
						if(mapy<0){
							mapy-=10;
						}
						mapCurAction=DOWN;
						draw();
					}
				}
				idleDraw();
				count += .0625;
				curFrame=(int)(count)%7;
			}
		}
		public int getMapX(){return mapx;}
		public int getMapY(){return mapy;}
	}
	@Override
	public void create () {
		batch = new SpriteBatch();
		Texture[][]mapFrames=new Texture[4][4];
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				mapFrames[i][j]=new Texture("mapFrames"+(i+1)+"-"+(j+1)+".png");
			}
		}
		Texture[][]sideFrames=new Texture[5][4];
		for(int i=0;i<5;i++){
			for(int j=0;j<4;j++){
				sideFrames[i][j]=new Texture("sideFrames"+(i+1)+"-"+(j+1)+".png");
			}
		}
		kat= new Kitten(200,200,mapFrames,sideFrames);
	}

	@Override
	public void render () {
		//Always stuff and then drawing
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//Only drawing here
		kat.moveAndDraw();
		kat.attackAndDraw();
		batch.end();
	}
}
