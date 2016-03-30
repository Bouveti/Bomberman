package com.ece.ing4.bomberman.controller;

import java.net.InetAddress;
import com.ece.ing4.bomberman.engine.*;

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

public class GameController {
	//@FXML private ListView<String> myList;
	//@FXML private TextField inputList;
	private Player player1;
	private Map newMap ;
	
	@FXML private AnchorPane anchor;
	@FXML private GridPane gPane;
	
	void initMap(Map observableList) throws Exception {
		
		for(int i=0;i<observableList.getHeight();i++) {
			for(int j=0;j<observableList.getWidth();j++){
				Label test = new Label();			
				String s = ""+observableList.getCell(i, j);
				
				if(s.compareTo("w")==0) test.setStyle("-fx-background-color: black;");	
				else if(s.compareTo("d")==0) test.setStyle("-fx-border-color:black;-fx-background-color: grey;");
				else if(s.compareTo("s")==0) test.setStyle("-fx-background-color: white;");
				else if(s.compareTo(" ")==0) test.setStyle("-fx-background-color: white;");
				test.setMinHeight(35);
				test.setMinWidth(35);
				test.setText("");
				gPane.add(test, i, j);
				//anchor.getChildren().add(test);
			}
			
		}
	}
}
