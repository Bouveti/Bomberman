package com.ece.ing4.bomberman.engine;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class Game implements Serializable{

	private ArrayList<Player> playerList;
	private Map map;
	
	public Game () {
		this.playerList = new ArrayList<Player>();
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
	
	public int play(){
		
		int win = -1;
		
		for(int i=0; i<this.playerList.size();i++){
			this.playerList.get(i).spawn(i, this.map.getHeight());
		}
		
		while(win<0){
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
		}
		return win;
	}
}
