package com.ece.ing4.bomberman.engine;

import java.io.Serializable;
import java.util.ArrayList;

public class Character implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1096805370408956773L;
	private String name;
	private int score;
	
	private int maxBomb;
	ArrayList<Bomb> bombLaid;

	private int x;
	private int y;
	
	private int speed;
	private int life;
	public Character(String name, int x, int y) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.bombLaid = new ArrayList<Bomb>();
		
		this.score = 0;
		this.maxBomb = 1;
		this.life = 1;
		this.speed = 1;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getMaxBomb() {
		return maxBomb;
	}
	public void setMaxBomb(int maxBomb) {
		this.maxBomb = maxBomb;
	}
	public ArrayList<Bomb> getBombLaid() {
		return bombLaid;
	}
	//public void layBomb(int x, int y) {
	//	this.bombLaid.add(new Bomb(x,y,this.countMax));
	//}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getLife() {
		return life;
	}
	public void setLife(int life) {
		this.life = life;
	}
}
