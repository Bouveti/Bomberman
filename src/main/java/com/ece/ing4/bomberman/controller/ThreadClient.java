package com.ece.ing4.bomberman.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import com.ece.ing4.bomberman.engine.Game;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class ThreadClient implements Runnable {

	private Socket socket;
	private String name = "client";
	private Game game;
	private BufferedReader in;
    private PrintWriter out;
    private String host;
    private int port;
	private ObservableList<String> observableList;

	public ThreadClient(String IPAddress, int port, ObservableList<String> observableList,String playerName) throws IOException {
		this.host = IPAddress;
		this.observableList = observableList;
		this.port = port;
		this.name = playerName;
		InetAddress address = InetAddress.getByName(host);
		this.socket = new Socket(address, port);
	}

	public void run() {

		try {
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);

			String sendMessage = name + "\n";
			bw.write(sendMessage);
			bw.flush();
			System.out.println(sendMessage);

			// Get the return message from the server

			// InputStream ois = new InputStream(socket.getInputStream());
			// this.game = (Game) ois.readObject();
			// System.out.print("CLIENT " + game.getPlayers().toString());
			// System.out.print("CLIENT " + (String) ois.readObject());
			// Platform.runLater(() ->setListViewObs());
			while(true) {
				InputStream is = socket.getInputStream();
		        InputStreamReader isr = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(isr);
		        String message = br.readLine();
		        if (message !="")System.out.print("client : "+message);
		        
		        ObjectInputStream ois = 
	                     new ObjectInputStream(socket.getInputStream());

	            String s = (String)ois.readObject();
	            System.out.println("String is: '" + s + "'");

		        //Get the return message from the serverObjectInputStream ois = 
            	//ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            	//this.game = (Game) ois.readObject();
            	//System.out.print("Client : "+game.getPlayers().toString());
			}
			//String serverAddress = getServerAddress();
	        //Socket socket = new Socket(serverAddress, 9001);
	        
			

		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
