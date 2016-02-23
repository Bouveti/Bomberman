package com.ece.ing4.bomberman.engine;

public class Map {

	private char table[][];
	private int width;
	private int height;
	
	
	public Map(int width, int height){
		
		this.width = width;
		this.height = height;
		this.table = new char[this.width][this.height];
		
		for(int i=0;i<this.width;i++)this.table[i][0] = 'w';
		for(int i=0;i<this.width;i++)this.table[i][this.height] = 'w';
		for(int i=0;i<this.height;i++)this.table[0][i] = 'w';
		for(int i=0;i<this.height;i++)this.table[this.width][i] = 'w';
		
		this.table[1][1] = 's';
		this.table[1][this.height-1] = 's';
		this.table[this.width-1][1] = 's';
		this.table[this.width-1][this.height-1] = 's';
		
		
		for(int i=0;i<this.width;i++){
			for(int j=0;j<this.height;j++){
				if((i%2 == 0)&&(j%2 == 0))this.table[i][j] = 'w';
				else if ((this.table[i][j] != 'w')&&(this.table[i][j] != 's')){
					if(Math.random() > 0.5)this.table[i][j] = ' ';
					else this.table[i][j] = 'd';
				}
			} 
		}
	}
}
