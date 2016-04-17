package com.ece.ing4.bomberman.engine;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.ece.ing4.bomberman.controller.ThreadServer;

public class Game implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1074505702590621633L;
	/**
	 * 
	 */

	private ArrayList<Player> playerList;
	private ArrayList<Bomb> listBomb;
	private Map map;
	private boolean gameStarted = false;
	
	public Game () {
		this.playerList = new ArrayList<Player>();
		this.listBomb = new ArrayList<Bomb>();
	}
	
	
	public void setMap(int size){
		
		int height = 0;
		int width = 0;
		
		switch(size){
		case 1: height = width = 9;
				break;
		case 2: height = width = 15;
				break;
		case 3:	height = width = 19;
				break;
		default: ;
		}
		
		this.map = new Map(height, width);
	}
	
	public Map getMap() {
		return this.map;
	}
	
	public ArrayList<Player> getPlayers(){
		return this.playerList;
	}

	
	public int connect(String name){
		int res = 0;
		if(this.playerList.size() == 4) res = -1;
		else{
			res = this.playerList.size()+1;
			this.playerList.add(new Player(name));
		}
		return res;
	}
	
	public int disconnect(String name){
		int res = -1;
		
		for(int i=0;i<this.playerList.size();i++){
			if(this.playerList.get(i).getName().compareTo(name) == 0){
				this.playerList.remove(i);
				res = i;
			}
		}
		return res;
	}
	
	public int winner(){
		
		int win = -1;
		
		int alive = 0;
		int num = 10;
			
		for(int i=0; i<this.playerList.size();i++){
			if(this.playerList.get(i).getAlive()){
				alive++;
				num = i;
			}
		}
			
		if(alive<2){
			win = num;
		}
		
		return win;
	}

	public boolean getGameStarted() {
		return this.gameStarted;
	}
	
	public void doCmd(int id, String move) {
		if (move.compareTo("SPACE") == 0) setBomb(this.playerList.get(id).getCharact().getX(), this.playerList.get(id).getCharact().getY());
		else this.playerList.get(id).moveCharact(map,listBomb,move);
	}
	
	public void setGameStarted(boolean b) {
		this.gameStarted = b;
	}

	public ArrayList<Bomb> getListBomb() {
		return listBomb;
	}

	
	
	public void setBomb(int x, int y) {
		Bomb newBomb = new Bomb(3, x, y, listBomb.size());
		this.listBomb.add(newBomb);
		
	}
	
	public void explode(int index) {
		Bomb b = removeById(index);
		int x = b.getX();
		int y = b.getY();
		for(int i = 0; i< this.playerList.size();i++) {
			Player player = this.playerList.get(i);
			Character charac = player.getCharact();
			if (tryPlayerBomb(charac,x,y)) {
				player.setAlive(false);
			}		
		}
	}
	
	private Bomb removeById(int index) {
		// TODO Auto-generated method stub
		Bomb b = null;
		for(int i = 0;i<listBomb.size();i++) {
			if(index == listBomb.get(i).getId()) {
				b= listBomb.get(i);
				listBomb.remove(i);
			}
		}
		return b;
	}


	public boolean tryPlayerBomb(Character charac, int x, int y){
		boolean res = false;
		
		for(int j=0;j<4;j++){
			switch(j){
				case 0:res = this.checkInRadius(charac, x, y, 1, 0);
						break;
					
				case 1:res = this.checkInRadius(charac, x, y, -1, 0);
						break;
					
				case 2:res = this.checkInRadius(charac, x, y, 0, 1);
						break;
					
				case 3:res = this.checkInRadius(charac, x, y, 0, -1);
						break;
				default: ;
		
			}
			if(res)j = 10;
		}
		
		System.out.println(res);
		return res;
	}
	
	public boolean isInBound(int x, int y){
		boolean res = false;
		
		if((x < this.map.getHeight())&&(x > 0)&&(y < this.map.getHeight())&&(x > 0))res = true;
		
		return res;
	}
	
	public boolean checkInRadius(Character charac,int x, int y, int addX, int addY){
		boolean res = false;
		
		for(int i=0;i<4;i++){
			if(this.isInBound(x+(i*addX), y+(i*addY))){
				/*System.out.println("Charac in: "+charac.getX()+","+charac.getY());
				System.out.println("Check in: "+(x+(i*addX)+","+(y+(i*addY))));*/
				if((charac.getX() == (x+(i*addX)))&&(charac.getY() == y+(i*addY))){
					res = true;
				}
				if(this.map.getCell(x+(i*addX), y+(i*addY))== 'd')this.map.setCell(x+(i*addX), y+(i*addY),' ');
				else if(this.map.getCell(x+(i*addX), y+(i*addY))== 'w') i = 10;
			}
		}
		return res;
	}
}
