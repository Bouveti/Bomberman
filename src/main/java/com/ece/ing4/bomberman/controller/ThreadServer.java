package com.ece.ing4.bomberman.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPOutputStream;

import com.ece.ing4.bomberman.engine.Game;
import com.ece.ing4.bomberman.engine.Player;
import com.sun.corba.se.impl.orbutil.ObjectWriter;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.IOException;
import java.util.*;

public class ThreadServer implements Runnable {
	private final int port;
	private ServerSocketChannel ssc;
	private Selector selector;
	private ByteBuffer buf = ByteBuffer.allocate(256);
	private Game mainGame;
	private ArrayList<Socket> listClient;

	ThreadServer(int port, Game newGame) throws IOException {
		this.port = port;
		this.mainGame = newGame;
		this.ssc = ServerSocketChannel.open();
		this.ssc.socket().bind(new InetSocketAddress(port));
		this.ssc.configureBlocking(false);
		this.selector = Selector.open();
		this.ssc.register(selector, SelectionKey.OP_ACCEPT);
	}

	@Override
	public void run() {
		try {
			System.out.println("Server starting on port " + this.port);

			Iterator<SelectionKey> iter;
			SelectionKey key;
			while (this.ssc.isOpen()) {
				selector.select();
				iter = this.selector.selectedKeys().iterator();
				while (iter.hasNext()) {
					key = iter.next();
					iter.remove();
					if (key.isAcceptable())
						this.handleAccept(key);
					if (key.isReadable())
						this.handleRead(key);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
			e.printStackTrace();
		}
	}

	private final ByteBuffer welcomeBuf = ByteBuffer.wrap("Welcome to NioChat!\n".getBytes());

	private void handleAccept(SelectionKey key) throws IOException {
		SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
		String address = (new StringBuilder(sc.socket().getInetAddress().toString())).append(":")
				.append(sc.socket().getPort()).toString();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ, address);
		//sc.write(welcomeBuf);
		//welcomeBuf.rewind();
		System.out.println("accepted connection from: " + address);
		broadcast();
	}

	private void handleRead(SelectionKey key) throws IOException {
		SocketChannel ch = (SocketChannel) key.channel();
		StringBuilder sb = new StringBuilder();

		buf.clear();
		int read = 0;
		while ((read = ch.read(buf)) > 0) {
			buf.flip();
			byte[] bytes = new byte[buf.limit()];
			buf.get(bytes);
			sb.append(new String(bytes));
			if(sb.toString().substring(0, 3).compareTo("map") == 0) {
				System.out.println(Integer.parseInt(sb.toString().substring(sb.toString().length()-1, sb.toString().length())));
				this.mainGame.setMap(Integer.parseInt(sb.toString().substring(sb.toString().length()-1, sb.toString().length())));
				this.mainGame.setGameStarted(true);
				placePlayer();
			}
			if(sb.toString().substring(0, 4).compareTo("CMD:") == 0) {
				System.out.println(Integer.parseInt(sb.toString().substring(sb.toString().length()-1, sb.toString().length())));
				this.mainGame.setMap(Integer.parseInt(sb.toString().substring(sb.toString().length()-1, sb.toString().length())));
				this.mainGame.setGameStarted(true);
				placePlayer();
			}else {
				this.mainGame.getPlayers().add(new Player(sb.toString()));
				System.out.println(mainGame.getGameStarted());
			}
			
			//Platform.runLater(() -> this.observableList.add(sb.toString()));
			buf.clear();
		}
		broadcast();
	}

	private void placePlayer() {
		for(int i = 0;i<mainGame.getPlayers().size();i++)
		this.mainGame.getPlayers().get(i).spawn(i, mainGame.getMap().getHeight());
	}

	private void broadcast() throws IOException {
		ByteBuffer writeBuffer = ByteBuffer.wrap(getBytes(mainGame));

		for (SelectionKey key : selector.keys()) {
			if (key.isValid() && key.channel() instanceof SocketChannel) {
				SocketChannel sch = (SocketChannel) key.channel();
				sch.write(writeBuffer);
				writeBuffer.rewind();
			}
		}
	}

	public static byte[] getBytes(Object obj) throws java.io.IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bos.close();
		byte[] data = bos.toByteArray();
		return data;
	}
}

/*
 * package com.ece.ing4.bomberman.controller;
 * 
 * import java.io.BufferedReader; import java.io.BufferedWriter; import
 * java.io.IOException; import java.io.InputStream; import
 * java.io.InputStreamReader; import java.io.ObjectOutputStream; import
 * java.io.OutputStream; import java.io.OutputStreamWriter; import
 * java.net.InetAddress; import java.net.InetSocketAddress; import
 * java.net.ServerSocket; import java.net.Socket; import java.nio.ByteBuffer;
 * import java.nio.CharBuffer; import java.nio.channels.SelectionKey; import
 * java.nio.channels.Selector; import java.nio.channels.ServerSocketChannel;
 * import java.nio.channels.SocketChannel; import java.nio.charset.Charset;
 * import java.nio.charset.CharsetDecoder; import
 * java.nio.charset.CharsetEncoder; import java.nio.charset.StandardCharsets;
 * import java.util.ArrayList; import java.util.Iterator; import java.util.Set;
 * 
 * import com.ece.ing4.bomberman.engine.Game;
 * 
 * import javafx.application.Platform; import javafx.collections.FXCollections;
 * import javafx.collections.ObservableList; import
 * javafx.scene.control.ListView;
 * 
 * public class ThreadServer implements Runnable{
 * 
 * private Game mainGame; private static Socket socket; private
 * ArrayList<Socket> listClient; private ObservableList<String> obsPlayer;
 * private int port; private int count = 0;
 * 
 * public ThreadServer (Game game, ObservableList<String> observableList){
 * this.listClient = new ArrayList<Socket>(); this.mainGame = game;
 * this.obsPlayer = observableList; this.port = 25000; }
 * 
 * public void run() {
 * 
 * Selector selector; try { selector = Selector.open();
 * 
 * ByteBuffer bbuf = ByteBuffer.allocate(8192); Charset charset =
 * Charset.forName("UTF-8"); CharsetDecoder decoder = charset.newDecoder();
 * CharsetEncoder encoder = charset.newEncoder(); InetSocketAddress
 * socketAddress = new InetSocketAddress(InetAddress.getLocalHost(),2009);
 * 
 * ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
 * serverSocketChannel.socket().bind(socketAddress);
 * serverSocketChannel.configureBlocking(false);
 * serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
 * 
 * while(true){ selector.select(); Set<SelectionKey> keys =
 * selector.selectedKeys(); Iterator<SelectionKey> keyIterator =
 * keys.iterator(); while(keyIterator.hasNext()) { SelectionKey key =
 * (SelectionKey) keyIterator.next(); keyIterator.remove();
 * 
 * if (key.isAcceptable()) { SocketChannel client =
 * serverSocketChannel.accept(); System.out.println(
 * "Serveur : Accept connection"); client.configureBlocking(false);
 * client.register(selector, SelectionKey.OP_READ); } else if (key.isReadable())
 * { // read SocketChannel client = (SocketChannel) key.channel(); ByteBuffer
 * inBuf = ByteBuffer.allocate(150); int bytesread = client.read(inBuf); if
 * (bytesread == -1) { key.cancel(); client.close(); continue; } inBuf.flip();
 * String request = decoder.decode(inBuf).toString(); System.out.println(
 * "Serveur : "+request); inBuf.clear();
 * 
 * client.register(selector, SelectionKey.OP_WRITE); } else if
 * (key.isWritable()) { this.count++; SocketChannel client = (SocketChannel)
 * key.channel(); String response = "hi - from non-blocking server"+count;
 * byte[] bs = response.getBytes(StandardCharsets.UTF_8); ByteBuffer buffer =
 * ByteBuffer.wrap(bs); client.write(buffer);
 * 
 * // switch to read, and disable write, client.register(selector,
 * SelectionKey.OP_READ); } } } }catch (IOException e) { // TODO Auto-generated
 * catch block e.printStackTrace(); } } }
 * 
 * 
 * /* } ServerSocketChannel ServerChannel; try { ServerChannel =
 * ServerSocketChannel.open();
 * 
 * ServerChannel.configureBlocking(false); ServerSocket Server =
 * ServerChannel.socket(); Server.bind(new InetSocketAddress(port)); Selector
 * selector = Selector.open(); ServerChannel.register(selector,
 * SelectionKey.OP_ACCEPT); System.out.println(
 * "Server began listening on port: " + port);
 * 
 * while (true) { int num = selector.select(); if (num == 0) { continue; }
 * ByteBuffer bbuf = ByteBuffer.allocate(8192); Set<SelectionKey> keys =
 * selector.selectedKeys(); Iterator<SelectionKey> it = keys.iterator(); while
 * (it.hasNext()) {
 * 
 * SelectionKey key = it.next(); if (key.isAcceptable()) { SocketChannel
 * ClientChannel = Server.accept().getChannel();
 * ClientChannel.configureBlocking(false); ClientChannel.register(selector,
 * SelectionKey.OP_READ); } if (key.isReadable()) { ((SocketChannel)
 * key.channel()).read(bbuf); Charset charset = Charset.forName("UTF-8");
 * bbuf.flip(); CharBuffer cbuf = charset.decode(bbuf);
 * System.out.println(cbuf); bbuf.compact();
 * 
 * } else { if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ)
 * { SocketChannel Client = null; Client = (SocketChannel) key.channel(); //
 * client=Client; // ReadClientStream(); } } it.remove(); // Add this to Remove
 * Already Selected SelectionKeys
 * 
 * } } } catch (IOException e1) { // TODO Auto-generated catch block
 * e1.printStackTrace(); } } }
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * /*
 * 
 * try {
 * 
 * //Server is running always. This is done using this while(true) loop int port
 * = 25000; ServerSocket serverSocket = new ServerSocket(port,4);
 * System.out.println("Server Started and listening to the port "+port);
 * System.out.println("got this brah : "
 * +mainGame.getPlayers().get(0).getName()); while(true) { //Reading the message
 * from the client socket = serverSocket.accept(); this.listClient.add(socket);
 * InputStream is = socket.getInputStream(); InputStreamReader isr = new
 * InputStreamReader(is); BufferedReader br = new BufferedReader(isr); String
 * nomJoueur = br.readLine(); System.out.println(
 * "Message received from client is "+nomJoueur);
 * this.mainGame.connect(nomJoueur);
 * 
 * //Multiplying the number by 2 and forming the return message String
 * returnMessage; returnMessage = "Jai bien reçu merci gros\n";
 * 
 * //Sending the response back to the client. for(int j =
 * 0;j<listClient.size();j++) { ObjectOutputStream oos; OutputStream os =
 * listClient.get(j).getOutputStream(); oos = new ObjectOutputStream(os);
 * oos.writeObject(mainGame); oos.flush(); System.out.println("SERVEUR : "
 * +mainGame.getPlayers().get(mainGame.getPlayers().size()-1).getName()); }
 * 
 * Platform.runLater(()
 * ->this.obsPlayer.add(mainGame.getPlayers().get(mainGame.getPlayers().size()-1
 * ).getName())); } } catch (Exception e) { e.printStackTrace(); } finally { try
 * { socket.close(); } catch(Exception e){} }
 */

// return mainGame.getMap();*/
