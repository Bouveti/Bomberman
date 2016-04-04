package com.ece.ing4.bomberman.engine;

import java.io.Serializable;

public class Player implements Serializable{

	private Character character;
	private String name;
	private boolean alive;
	
	public Player (String s) {
		this.name = s;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void spawn(int number, int mapSize){
		int x = 0;
		int y = 0;
		
		switch(number){
		case 0: x = y = 1;
				break;
		case 1: x = mapSize-2;
				y = 1;
				break;
		case 2:	x = mapSize-2;
				y = 1;
				break;
		case 3: x = mapSize-2;
				y = mapSize-2;
				break;
		default: ;
		}
		
		this.character = new Character(this.name, x, y);
	}
	
	public boolean getAlive(){
		return this.alive;
	}
	
	public void setAlive(boolean alive){
		this.alive = alive;
	}
}