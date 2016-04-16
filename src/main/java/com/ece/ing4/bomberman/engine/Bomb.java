package com.ece.ing4.bomberman.engine;

import java.io.Serializable;

public class Bomb implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2652461463082087237L;
	private int count;
	private int x;
	private int y;
	private int id;
	
	
	public Bomb(int count, int x, int y, int id) {
		super();
		this.setCount(count);
		this.setX(x);
		this.setY(y);
		this.setId(id);
	}


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


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	
	
	
}
