package com.ece.ing4.bomberman.test;

import com.ece.ing4.bomberman.engine.Map;

public class MapTest {

	public static void main(String[] args) {
		int width = 9;
		int height = 9;
		Map map = new Map(height,width);
		
		String line = "";
		
		for(int i=0;i<map.getHeight();i++)System.out.println(map.getLine(i));

	}

}
