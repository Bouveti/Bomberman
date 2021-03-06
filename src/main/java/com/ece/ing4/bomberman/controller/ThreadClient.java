package com.ece.ing4.bomberman.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import com.ece.ing4.bomberman.engine.Game;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;

//Cr�er le thread qui permet de se connecter en tant que client

public class ThreadClient implements Runnable {

	private Socket socket;
	private String name = "client";
	private int idJoueur = -1;
	private String host;
	private int port;
	private boolean receiving = true;
	private ObservableList<String> observableList;
	private final BlockingQueue<String> messageQueue;
	private final BlockingQueue<String> portQueue;
	private final BlockingQueue<String> cmdQueue;
	private final BlockingQueue<Integer> idQueue;
	private final BlockingQueue<Game> gameQueue;

	public ThreadClient(String IPAddress, int port, ObservableList<String> observableList,
			BlockingQueue<String> messageQueue, BlockingQueue<String> portQueue, BlockingQueue<String> cmdQueue,
			BlockingQueue<Game> gameQueue, BlockingQueue<Integer> idQueue, String playerName)
					throws IOException, InterruptedException {
		this.host = IPAddress;
		this.observableList = observableList;
		this.port = port;
		this.name = playerName;
		this.messageQueue = messageQueue;
		this.cmdQueue = cmdQueue;
		this.portQueue = portQueue;
		this.gameQueue = gameQueue;
		this.idQueue = idQueue;
		InetAddress address = InetAddress.getByName(host);
		this.socket = new Socket(address, port);
		disconnect();
	}

	protected void disconnect() {
		final LongProperty lastUpdate = new SimpleLongProperty();
		final long minUpdateInterval = 0;

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (now - lastUpdate.get() > minUpdateInterval) {
					final String message = cmdQueue.poll();
					if (message != null) {
						System.out.println("Client : " + message.substring(0, 3));
						if (message == "deco")
							try {
								socket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						if (message.substring(0, 3).compareTo("map") == 0)
							try {
								writeToServer(message);
							} catch (IOException e) {
								e.printStackTrace();
							}
						lastUpdate.set(now);
					}
				}
			}
		};
		timer.start();
	}

	public void run() {
		try {
			writeToServer(name + "\n");
			while (receiving) {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Game mainGame = (Game) ois.readObject();
				final String nbJoueurs = mainGame.getPlayers().size() + " / 4";
				final String portAdd = this.port + "";
				if (idJoueur == -1) {
					this.idJoueur = mainGame.getPlayers().size();
					idQueue.put(idJoueur);
				}

				System.out.println("My ID : " + idJoueur);

				if (!mainGame.getGameStarted()) {
					messageQueue.put(nbJoueurs);
					portQueue.put(portAdd);
				}

				gameQueue.put(mainGame);
				Platform.runLater(() -> setListViewObs(mainGame));

			}

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

	protected void writeToServer(String s) throws IOException {
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(s);
		bw.flush();
		
		if(s.toString().substring(5, s.toString().length()).compareTo("SERVERQUIT") == 0) {
			System.out.println("CLOSE SERVER");
			socket.close();
		}
	}

	private Object setListViewObs(Game mainGame) {
		this.observableList.clear();
		for (int i = 0; i < mainGame.getPlayers().size(); i++)
			this.observableList.add(mainGame.getPlayers().get(i).getName());
		return null;
	}

	public BlockingQueue<String> getQueue() {
		return this.cmdQueue;
	}

	public BlockingQueue<Game> getGameQueue() {
		return this.gameQueue;
	}
}
