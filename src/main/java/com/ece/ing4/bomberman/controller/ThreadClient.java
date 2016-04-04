package com.ece.ing4.bomberman.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.ece.ing4.bomberman.engine.Game;

public class ThreadClient implements Runnable{

	private static Socket socket;
	private String name;
	private Game game;
	
	public ThreadClient (String s){
		this.name = s;
	}
	
	
	@Override
	public void run() {
		
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
 
            String sendMessage =name+"\n";
            bw.write(sendMessage);
            bw.flush();
            System.out.println(sendMessage);
        
            	//Get the return message from the serverObjectInputStream ois = 
            	ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            	this.game = (Game) ois.readObject();
            	System.out.print(game.getPlayers().toString());
            
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
