package com.ece.ing4.bomberman.engine;

import javax.swing.text.TabableView;

public class Map {

	private char table[][];
	private int width;
	private int height;
	
	
	public Map(int height, int width){
		
		this.width = width+2;
		this.height = height+2;
		this.table = new char[this.height][this.width];
		
		for(int i=0;i<this.width;i++)this.table[0][i] = 'w';
		for(int i=0;i<this.width;i++)this.table[this.height-1][i] = 'w';
		for(int i=0;i<this.height;i++)this.table[i][0] = 'w';
		for(int i=0;i<this.height;i++)this.table[i][this.width-1] = 'w';
		
		this.table[1][1] = 's';
		this.table[2][1] = 's';
		this.table[1][2] = 's';
		
		this.table[this.height-2][1] = 's';
		this.table[this.height-2][2] = 's';
		this.table[this.height-3][1] = 's';
		
		
		this.table[1][this.width-2] = 's';
		this.table[2][this.width-2] = 's';
		this.table[1][this.width-3] = 's';
		
		this.table[this.height-2][this.width-2] = 's';
		this.table[this.height-3][this.width-2] = 's';
		this.table[this.height-2][this.width-3] = 's';
		
		
		for(int i=0;i<this.height;i++){
			for(int j=0;j<this.width;j++){
				if((i%2 == 0)&&(j%2 == 0))this.table[i][j] = 'w';
				else if ((this.table[i][j] != 'w')&&(this.table[i][j] != 's')){
					if(Math.random() > 0.7)this.table[i][j] = ' ';
					else this.table[i][j] = 'd';
				}
			} 
		}
	}
	
	public char[][] getMap(){
		return this.table;
	}
	
	public char getCell(int x, int y){
		return this.table[x][y];
	}
	
	public char[] getLine(int x){
		return this.table[x];
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
}