package com.ece.ing4.bomberman.controller;

import java.net.InetAddress;
import com.ece.ing4.bomberman.engine.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LauncherController {
	//@FXML private ListView<String> myList;
	//@FXML private TextField inputList;
	private Player player1;
	
	@FXML private TextField playerName;
	@FXML private Label playerHint;
	@FXML private Button createGame;
	@FXML private Button cancelGame;
	@FXML private Button joinGame;
	@FXML private ListView<String> playerList;
	@FXML private Label nbJoueurs;
	@FXML private Label ipAddress;
	
	private boolean createPlayer() {
		if(!playerName.getText().contentEquals("")) {
			this.player1 = new Player(playerName.getText());
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
			     controller.initData(FXCollections.observableArrayList(this.player1.getName()));
		    }
		    else{
		    	 stage = (Stage) joinGame.getScene().getWindow();
		         fxmlLoader = new FXMLLoader(getClass().getResource("../view/RoomJoin.fxml"));
		         //root = FXMLLoader.load(getClass().getResource("../view/RoomJoin.fxml"));
		         root = (Parent)fxmlLoader.load();    
		    }
		    	
		      	Scene scene = new Scene(root);
		      	stage.setScene(scene);
		      	stage.show();
		}
	}

	private void initData(ObservableList<String> observableList) throws Exception {
		playerList.setItems(observableList);
		nbJoueurs.setText(playerList.getItems().size()+" / 4");
		ipAddress.setText(InetAddress.getLocalHost().getHostAddress());
	}

	@FXML
	private void launchGame() throws Exception {
		Stage stage; 
	    Parent root;
	    stage = (Stage) cancelGame.getScene().getWindow();
	    root = FXMLLoader.load(getClass().getResource("../view/Game.fxml"));
	    Scene scene = new Scene(root);
      	stage.setScene(scene);
      	stage.show();
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
