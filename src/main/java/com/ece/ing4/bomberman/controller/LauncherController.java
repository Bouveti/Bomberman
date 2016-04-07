package com.ece.ing4.bomberman.controller;

import java.net.InetAddress;
import com.ece.ing4.bomberman.engine.*;
import com.ece.ing4.bomberman.engine.Character;

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

public class LauncherController {
	//@FXML private ListView<String> myList;
	//@FXML private TextField inputList;
	private Player player1;
	private Map newMap ;
	private Game newGame;
	private ThreadServer server;
	
	@FXML private TextField playerName;
	@FXML private Label playerHint;
	@FXML private Button createGame;
	@FXML private Button cancelGame;
	@FXML private Button joinGame;
	@FXML private ListView<String> playerList;
	@FXML private Label nbJoueurs;
	@FXML private Label ipAddress;
	@FXML private AnchorPane anchor;
	@FXML private RadioButton mapMediumRadio;
	@FXML private RadioButton mapLargeRadio;
	@FXML private RadioButton mapSmallRadio;
	
	private boolean createPlayer() {
		if(!playerName.getText().contentEquals("")) {
			
			this.player1 = new Player(playerName.getText());
			this.newGame = new Game();
		    //this.newGame.connect(player1.getName());
		    System.out.println(player1);
			return true;
			
		} else {
			playerHint.setVisible(true);
			return false;
		}
	}

	
	@FXML
	 private void handleButtonAction(ActionEvent event) throws Exception{
		if(createPlayer()) {
		    Stage stage; 
		    Parent root;
		    FXMLLoader fxmlLoader;
		    
		    if(event.getSource()==createGame){  
		    	 stage = (Stage) createGame.getScene().getWindow();
		         //root = FXMLLoader.load(getClass().getResource("../view/RoomCreate.fxml"));
		         fxmlLoader = new FXMLLoader(getClass().getResource("../view/RoomCreate.fxml"));
		         root = (Parent)fxmlLoader.load();    
		         LauncherController controller = fxmlLoader.<LauncherController>getController();
			     controller.initData(this.player1.getName(),newGame,true);
			     
		    }
		    else{
		    	 stage = (Stage) joinGame.getScene().getWindow();
		         fxmlLoader = new FXMLLoader(getClass().getResource("../view/RoomJoin.fxml"));
		         root = (Parent)fxmlLoader.load();    
		         LauncherController controller = fxmlLoader.<LauncherController>getController();
			     controller.initData(this.player1.getName(),newGame,false);
		         //root = FXMLLoader.load(getClass().getResource("../view/RoomJoin.fxml"));
			     
		    }
		     
		    Scene scene = new Scene(root);
		    stage.setScene(scene);
		    stage.show();
		}
	}

	
	@FXML
	private void launchGame() throws Exception {
		defineMap();
		Stage stage; 
	    Parent root;
		FXMLLoader fxmlLoader;
		stage = (Stage) cancelGame.getScene().getWindow();
		
		fxmlLoader = new FXMLLoader(getClass().getResource("../view/Game.fxml"));
		root = (Parent)fxmlLoader.load();
        GameController controller = fxmlLoader.<GameController>getController();
	    controller.initMap(this.newGame);
	    	   
	    Scene scene = new Scene(root);
	    scene.getRoot().requestFocus();
      	stage.setScene(scene);
      	stage.show(); 
    }
	
	private void defineMap() {
		System.out.println(this.playerList);
		if(mapSmallRadio.isSelected()) this.newGame.setMap(1);
		else if (mapMediumRadio.isSelected()) this.newGame.setMap(2);
		else this.newGame.setMap(3);
	}
	
	private void initData(String playerName, Game ng, boolean serverBool) throws Exception {
		this.newGame = ng;
		ObservableList<String> observableList = FXCollections.observableArrayList();
		this.playerList.setItems(observableList);
		this.nbJoueurs.setText(observableList.size()+" / 4");
		this.ipAddress.setText(InetAddress.getLocalHost().getHostAddress());

		if(serverBool) {
			//new Thread(new ThreadServer(newGame, observableList)).start();
			System.out.println("LAUNCHER : ");
			ThreadServer server = new ThreadServer(10523,newGame, observableList);
			(new Thread(server)).start();
		}
		ThreadClient server = new ThreadClient("localhost", 10523,observableList,playerName);
		new Thread(server).start();
	}
	

	@FXML
	private void cancelGame() throws Exception {
		Stage stage; 
	    Parent root;
	    stage = (Stage) cancelGame.getScene().getWindow();
	    root = FXMLLoader.load(getClass().getResource("../view/Launcher.fxml"));
	    Scene scene = new Scene(root);
      	stage.setScene(scene);
      	stage.show();
	}	
}
