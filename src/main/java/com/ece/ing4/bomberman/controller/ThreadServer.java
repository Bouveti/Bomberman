package com.ece.ing4.bomberman.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ece.ing4.bomberman.engine.Game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class ThreadServer implements Runnable{

	private Game mainGame;
	private static Socket socket;
	private ListView<String> playerList;
	private ObservableList<String> obsPlayer;
	
	public ThreadServer (Game game,ListView<String> pl,ObservableList<String> playerList2){
		System.out.println(playerList+" contrsuute");
		this.mainGame = game;
		this.obsPlayer = playerList2;
		this.playerList = pl;
		this.playerList.setItems(obsPlayer);
	}
	
	public void run() {
		try {
			 
            //Server is running always. This is done using this while(true) loop
            int port = 25000;
            ServerSocket serverSocket = new ServerSocket(port,4);
            System.out.println("Server Started and listening to the port "+port);
            System.out.println("got this brah : "+mainGame.getPlayers().get(0).getName());
            while(true)
            {
                //Reading the message from the client
                socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String nomJoueur = br.readLine();
                System.out.println("Message received from client is "+nomJoueur);
                this.mainGame.connect(nomJoueur);
 
                //Multiplying the number by 2 and forming the return message
                String returnMessage;               
                returnMessage = "Jai bien reçu merci gros\n";
 
                //Sending the response back to the client.
                ObjectOutputStream  oos;
                OutputStream os =  socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.writeObject(mainGame);
				oos.flush();
                System.out.println(mainGame.getPlayers().toString());
                
                this.obsPlayer.add("JE SUIS GREAG");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch(Exception e){}
        }
	}

}
//return mainGame.getMap();