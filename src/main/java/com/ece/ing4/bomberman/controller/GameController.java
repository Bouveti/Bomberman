package com.ece.ing4.bomberman.controller;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.ece.ing4.bomberman.engine.*;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class GameController {

	private Game theGame;
	private int idJoueur;

	private ThreadClient client;
	BlockingQueue<Game> gameQueue = new ArrayBlockingQueue<>(1);

	@FXML
	private AnchorPane anchor;
	@FXML
	private GridPane gPane;

	void initMap(Game newGame, int idJoueur, ThreadClient client) throws Exception {
		this.theGame = newGame;
		this.idJoueur = idJoueur;
		this.client = client;
		this.gameQueue = client.getGameQueue();
		drawMap();
	}

	private AnimationTimer waitGameInput() {
		final LongProperty lastUpdate = new SimpleLongProperty();
		final long minUpdateInterval = 0;

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (now - lastUpdate.get() > minUpdateInterval) {
					final Game mainGame = gameQueue.poll();
					if (mainGame != null && mainGame.getPlayers().size() > 0) {
						theGame = mainGame;
						drawMap();
						this.stop();
					}
					lastUpdate.set(now);
				}
			}
		};
		return timer;
	}

	private void drawMap() {

		AnimationTimer timer = waitGameInput();
		timer.start();
		Map myMap = theGame.getMap();
		gPane.getChildren().clear();
		for (int i = 0; i < myMap.getHeight(); i++) {
			for (int j = 0; j < myMap.getWidth(); j++) {
				Label caseMap = new Label();
				String s = "" + myMap.getCell(i, j);
				caseMap.setText("");
				if (s.compareTo("w") == 0)
					caseMap.setStyle("-fx-background-color: black;");
				else if (s.compareTo("d") == 0)
					caseMap.setStyle("-fx-border-color:black;-fx-background-color: grey;");
				else if (s.compareTo("s") == 0)
					caseMap.setStyle("-fx-background-color: white;-fx-alignment: center;");
				else if (s.compareTo(" ") == 0)
					caseMap.setStyle("-fx-background-color: white;");
				caseMap.setMinHeight(35);
				caseMap.setMinWidth(35);

				gPane.add(caseMap, j, i);
			}
		}
		for (int b = 0; b<theGame.getListBomb().size();b++)
		{
			int x = theGame.getListBomb().get(b).getX();
			int y = theGame.getListBomb().get(b).getY();
			Label bomb = new Label();
			bomb.setText("Ò");
			bomb.setStyle("-fx-alignment: center;-fx-font-size: 18pt;");
			bomb.setMinHeight(35);
			bomb.setMinWidth(35);
			gPane.add(bomb, y, x);
			
		}
		for (int k = 0; k < theGame.getPlayers().size(); k++) {
			if (theGame.getPlayers().get(k).getAlive()) {
				int x = theGame.getPlayers().get(k).getCharact().getX();
				int y = theGame.getPlayers().get(k).getCharact().getY();
				Label node = (Label) getNodeByRowColumnIndex(x, y, gPane);
				if (k == 0)
					node.setStyle("-fx-border-color:black;-fx-background-color: red;-fx-alignment: center;");
				if (k == 1)
					node.setStyle("-fx-border-color:black;-fx-background-color: green;-fx-alignment: center;");
				if (k == 2)
					node.setStyle("-fx-border-color:black;-fx-background-color: blue;-fx-alignment: center;");
				if (k == 3)
					node.setStyle("-fx-border-color:black;-fx-background-color: yellow;-fx-alignment: center;");
				node.setText("P" + k);
			}
		}
	}

	public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
		Node result = null;
		ObservableList<Node> childrens = gridPane.getChildren();
		for (Node node : childrens) {
			if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
				result = node;
				break;
			}
		}
		return result;
	}

	@FXML
	private void keyPressed(KeyEvent evt) throws IOException {
		String s = "CMD:" + this.idJoueur + "" + evt.getCode();
		System.out.println(s);
		sendCmd(s);
	}

	private void sendCmd(String result) throws IOException {
		this.client.writeToServer(result);
	}
}
