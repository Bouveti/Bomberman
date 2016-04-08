package com.ece.ing4.bomberman.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.ece.ing4.bomberman.engine.*;
import com.ece.ing4.bomberman.engine.Character;

import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class GameController {
	
	private Game theGame;
	private int idJoueur;
	
	private ThreadClient client;
	BlockingQueue<Game> gameQueue = new ArrayBlockingQueue<>(1);
	
	@FXML private AnchorPane anchor;
	@FXML private GridPane gPane;
	
	void initMap(Game newGame, int idJoueur, ThreadClient client) throws Exception {
		this.theGame = newGame;
		this.idJoueur = idJoueur;
		this.client = client;
		this.gameQueue = client.getGameQueue();
		drawMap();
	}
	
	private AnimationTimer waitGameInput() {
		final LongProperty lastUpdate = new SimpleLongProperty();
        final long minUpdateInterval = 0 ; // nanoseconds. Set to higher number to slow output.

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    final Game mainGame = gameQueue.poll();
                    if (mainGame != null && mainGame.getPlayers().size()>0) {
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
		
		AnimationTimer timer= waitGameInput();
		timer.start();
		Map myMap = theGame.getMap();
		for(int i=0;i<myMap.getHeight();i++) {
			for(int j=0;j<myMap.getWidth();j++){
				Label caseMap = new Label();			
				String s = ""+myMap.getCell(i, j);
				if(s.compareTo("w")==0) caseMap.setStyle("-fx-background-color: black;");	
				else if(s.compareTo("d")==0) caseMap.setStyle("-fx-border-color:black;-fx-background-color: grey;");
				else if(s.compareTo("s")==0) caseMap.setStyle("-fx-background-color: white;-fx-alignment: center;");
				else if(s.compareTo(" ")==0) caseMap.setStyle("-fx-background-color: white;");
				caseMap.setMinHeight(35);
				caseMap.setMinWidth(35);
				caseMap.setText("");
				gPane.add(caseMap, i, j);
			}
		}
		System.out.println("SIZE : "+theGame.getPlayers().size());
		for(int k=0;k<theGame.getPlayers().size();k++) {
			int x = theGame.getPlayers().get(k).getCharact().getX();
			int y = theGame.getPlayers().get(k).getCharact().getY();
			System.out.println("JE DESSINE EN : "+x+"/"+y);
			Label node = (Label) getNodeByRowColumnIndex(x,y,gPane);
			node.setText("P"+k);
		}
	}

	public Node getNodeByRowColumnIndex(final int row,final int column,GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();
        for(Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }
	@FXML
    private void keyPressed(KeyEvent evt) throws IOException {
		String s = "CMD:"+idJoueur+""+evt.getCode();
		sendCmd(s);
	}
		
	private void sendCmd(String result) throws IOException {
		this.client.writeToServer(result);
	}
}	

