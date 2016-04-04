package com.ece.ing4.bomberman.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class GameController {
	
	//@FXML private ListView<String> myList;
	//@FXML private TextField inputList;
	private Character player1;
	private static Socket socket;
	private Map newMap ;
	private Game theGame;
	
	@FXML private AnchorPane anchor;
	@FXML private GridPane gPane;
	
	void initMap(Game newGame) throws Exception {
		this.theGame = newGame;
		Map myMap = theGame.getMap();
		for(int i=0;i<myMap.getHeight();i++) {
			for(int j=0;j<myMap.getWidth();j++){
				Label test = new Label();			
				String s = ""+myMap.getCell(i, j);
				if(s.compareTo("w")==0) test.setStyle("-fx-background-color: black;");	
				else if(s.compareTo("d")==0) test.setStyle("-fx-border-color:black;-fx-background-color: grey;");
				else if(s.compareTo("s")==0) test.setStyle("-fx-background-color: white;");
				else if(s.compareTo(" ")==0) test.setStyle("-fx-background-color: white;");
				test.setMinHeight(35);
				test.setMinWidth(35);
				test.setText("");
				gPane.add(test, i, j);
			}
		}
	}
	@FXML
    private void keyPressed(KeyEvent evt) throws IOException {
		String s = ""+evt.getCode();
		char result = 0;
		switch (s) {
			case "W" : result = 'z';
				sendCmd(result);
				break;
			case "A" : result = 'q';
				sendCmd(result);
				break;
			case "S" : result = 's';
				sendCmd(result);
				break;
			case "D" : result = 'd';
				sendCmd(result);
				break;
			case "SPACE" : result = 'b';
				sendCmd(result);
				break;
			default : break;
		}
	}
		
	private void sendCmd(char result) {
		try
        {
            String host = "localhost";
            int port = 25000;
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);
 
            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
 
            String sendMessage = result + "\n";
            bw.write(sendMessage);
            bw.flush();
            System.out.println(sendMessage);
 
            //Get the return message from the server
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            System.out.print(message);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            //Closing the socket
            try
            {
                socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
		
	}	

