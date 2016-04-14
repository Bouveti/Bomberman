package com.ece.ing4.bomberman.engine;

import java.io.Serializable;

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

	public void moveCharact(Map map, String move) {
		switch (move) {
		case "A":
			if (map.getCell(this.character.getX(), (this.character.getY() - 1)) == ' ') {
				this.character.setY(this.character.getY() - 1);
			} 
			break;
		case "D":
			if (map.getCell(this.character.getX(), (this.character.getY() + 1)) == ' ') {
				this.character.setY(this.character.getY() + 1);
			} 
			break;
		case "W":
			if (map.getCell(this.character.getX() - 1, (this.character.getY())) == ' ') {
				this.character.setX(this.character.getX() - 1);
			} 
			break;
		case "S":
			if (map.getCell(this.character.getX() + 1, (this.character.getY())) == ' ') {
				this.character.setX(this.character.getX() + 1);
			} 
			break;
		}

	}
}