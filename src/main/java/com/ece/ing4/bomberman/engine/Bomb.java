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
	
	
	public Bomb(int count, int x, int y) {
		super();
		this.setCount(count);
		this.setX(x);
		this.setY(y);
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
	
	
	
}
