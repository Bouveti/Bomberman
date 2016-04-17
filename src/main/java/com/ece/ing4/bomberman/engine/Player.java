package com.ece.ing4.bomberman.engine;

import java.io.Serializable;
import java.util.ArrayList;

//Classe qui représente un joueur avec son nom

public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6806907692586089400L;
	private Character character;
	private String name;
	private boolean alive;
	private int id;

	public Player(String s) {
		this.name = s;
		this.alive = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void spawn(int number, int mapSize) {
		int x = 0;
		int y = 0;

		switch (number) {
		case 0:
			x = y = 1;
			break;
		case 1:
			x = mapSize - 2;
			y = 1;
			break;
		case 2:
			x = 1;
			y = mapSize - 2;
			break;
		case 3:
			x = mapSize - 2;
			y = mapSize - 2;
			break;
		default:
			;
		}

		this.character = new Character(this.name, x, y);
	}

	public boolean getAlive() {
		return this.alive;
	}

	public Character getCharact() {
		return this.character;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void moveCharact(Map map, ArrayList<Bomb> listBomb, String move) {
		int x = this.character.getX();
		int y = this.character.getY();
		switch (move) {
		case "A":
		case "LEFT":
			if (map.getCell(x, (y - 1)) == ' ' &&
			checkBomb(listBomb, x ,y-1)) {
				this.character.setY(y - 1);
			} 
			break;
		case "D":
		case "RIGHT":
			if (map.getCell(x, (y + 1)) == ' ' &&
					checkBomb(listBomb, x ,y+1)) {
				this.character.setY(y + 1);
			} 
			break;
		case "W":
		case "UP":
			if (map.getCell(x - 1, (y)) == ' ' &&
					checkBomb(listBomb, x-1 ,y)) {
				this.character.setX(x - 1);
			} 
			break;
		case "S":
		case "DOWN":
			if (map.getCell(x + 1, (y)) == ' ' &&
					checkBomb(listBomb, x+1 ,y)) {
				this.character.setX(x + 1);
			} 
			break;
		}

	}
	
	private boolean checkBomb(ArrayList<Bomb> lb, int x, int y) {
		for (int k = 0 ; k<lb.size();k++) {
			if (lb.get(k).getX() == x && lb.get(k).getY() == y) return false;
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}