package com.ece.ing4.bomberman.controller;

import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.ece.ing4.bomberman.engine.*;

import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LauncherController {
	private Player player1;
	private Game newGame;
	private String ipHost;
	private int portHost;
	private int idJoueur;

	private ThreadClient client;
	private ThreadServer server;

	@FXML
	private TextField playerName;
	@FXML
	private Label playerHint;
	@FXML
	private Button createGame;
	@FXML
	private Button cancelGame;
	@FXML
	private Button joinGame;
	@FXML
	private ListView<String> playerList;
	@FXML
	private Label nbJoueurs;
	@FXML
	private Label ipAddress;
	@FXML
	private Label portAddress;
	@FXML
	private AnchorPane anchor;
	@FXML
	private RadioButton mapMediumRadio;
	@FXML
	private RadioButton mapLargeRadio;
	@FXML
	private RadioButton mapSmallRadio;
	@FXML
	private TextField ip;
	@FXML
	private TextField port;
	@FXML
	private Label ipHint;

	BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(1);
	BlockingQueue<String> cmdQueue = new ArrayBlockingQueue<>(1);
	BlockingQueue<String> portQueue = new ArrayBlockingQueue<>(1);
	BlockingQueue<Integer> idQueue = new ArrayBlockingQueue<>(1);
	BlockingQueue<Game> gameQueue = new ArrayBlockingQueue<>(1);

	private boolean createPlayer() {
		if (!playerName.getText().contentEquals("")) {
			if (!ip.getText().contentEquals("") || !port.getText().contentEquals("")) {
				this.player1 = new Player(playerName.getText());
				this.newGame = new Game();
				this.ipHost = ip.getText();
				this.portHost = Integer.parseInt(port.getText());
				return true;
			} else {
				ipHint.setVisible(true);
				return false;
			}

		} else {
			playerHint.setVisible(true);
			return false;
		}
	}

	@FXML
	private void handleButtonAction(ActionEvent event) throws Exception {
		if (createPlayer()) {
			Stage stage;
			Parent root;
			FXMLLoader fxmlLoader;

			if (event.getSource() == createGame) {
				stage = (Stage) createGame.getScene().getWindow();
				fxmlLoader = new FXMLLoader(getClass().getResource("../view/RoomCreate.fxml"));
				root = (Parent) fxmlLoader.load();
				LauncherController controller = fxmlLoader.<LauncherController> getController();
				controller.initData(this.player1.getName(), newGame, this.portHost, this.ipHost, this.messageQueue,
						this.portQueue, this.gameQueue, this.cmdQueue, this.idQueue, true);

			} else {
				stage = (Stage) joinGame.getScene().getWindow();
				fxmlLoader = new FXMLLoader(getClass().getResource("../view/RoomJoin.fxml"));
				root = (Parent) fxmlLoader.load();
				LauncherController controller = fxmlLoader.<LauncherController> getController();
				controller.initData(this.player1.getName(), newGame, this.portHost, this.ipHost, this.messageQueue,
						this.portQueue, this.gameQueue, this.cmdQueue, this.idQueue, false);

			}

			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
	}

	@FXML
	private void launchGame() throws Exception {
		defineMap();
	}

	private void launchGameByClient() throws Exception {
		Stage stage;
		Parent root;
		FXMLLoader fxmlLoader;
		stage = (Stage) cancelGame.getScene().getWindow();

		fxmlLoader = new FXMLLoader(getClass().getResource("../view/Game.fxml"));
		root = (Parent) fxmlLoader.load();
		GameController controller = fxmlLoader.<GameController> getController();
		controller.initMap(this.newGame, this.idJoueur, this.client);

		Scene scene = new Scene(root);
		scene.getRoot().requestFocus();
		stage.setScene(scene);
		stage.show();
	}

	private void defineMap() throws InterruptedException {
		if (mapSmallRadio.isSelected()) {
			cmdQueue = client.getQueue();
			cmdQueue.put("map : 1");
		} else if (mapMediumRadio.isSelected()) {
			cmdQueue = client.getQueue();
			cmdQueue.put("map : 2");
		} else {
			cmdQueue = client.getQueue();
			cmdQueue.put("map : 3");
		}
	}

	private void initData(String playerName, Game ng, int portHost, String ipHost, BlockingQueue<String> messageQueue,
			BlockingQueue<String> portQueue, BlockingQueue<Game> gameQueue, BlockingQueue<String> cmdQueue,
			BlockingQueue<Integer> idQueue, boolean serverBool) throws Exception {
		this.newGame = ng;
		ObservableList<String> observableList = FXCollections.observableArrayList();
		this.playerList.setItems(observableList);
		this.nbJoueurs.setText(observableList.size() + " / 4");
		this.ipAddress.setText(InetAddress.getLocalHost().getHostAddress());

		if (serverBool) {
			System.out.println("LAUNCHER : " + portHost + ipHost);
			server = new ThreadServer(portHost, newGame);
			(new Thread(server)).start();
		}

		client = new ThreadClient(ipHost, portHost, observableList, messageQueue, portQueue, cmdQueue, gameQueue,
				idQueue, playerName);
		new Thread(client).start();

		final LongProperty lastUpdate = new SimpleLongProperty();
		final long minUpdateInterval = 0; // nanoseconds. Set to higher number
											// to slow output.

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (now - lastUpdate.get() > minUpdateInterval) {
					final String message = messageQueue.poll();
					final String portAdd = portQueue.poll();
					final Integer idJ = idQueue.poll();
					final Game mainGame = gameQueue.poll();
					if (idJ != null && (idJ > -1)) {
						System.out.println("ID LAUNCHER: " + idJ);
						idJoueur = idJ;
					}
					if (message != null) {
						nbJoueurs.setText(message);
					}
					if (portAdd != null) {
						System.out.println(portAdd);
						portAddress.setText(portAdd);
					}
					if (mainGame != null && mainGame.getPlayers().size() > 0) {
						System.out.println(mainGame.getPlayers().get(0).getName());
						newGame = mainGame;
						if (mainGame.getGameStarted())
							try {
								launchGameByClient();
								this.stop();
							} catch (Exception e) {
								e.printStackTrace();
							}

					}
					lastUpdate.set(now);
				}
			}
		};
		timer.start();
	}

	@FXML
	private void cancelGame() throws Exception {
		cmdQueue = client.getQueue();
		cmdQueue.put("deco");

		Stage stage;
		Parent root;
		stage = (Stage) cancelGame.getScene().getWindow();
		root = FXMLLoader.load(getClass().getResource("../view/Launcher.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
