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
		this.count = count;
		this.x = x;
		this.y = y;
	}
	
	
}
