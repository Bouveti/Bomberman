package com.ece.ing4.bomberman.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.ece.ing4.bomberman.engine.Bomb;
import com.ece.ing4.bomberman.engine.Game;
import com.ece.ing4.bomberman.engine.Player;

import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class ThreadServer implements Runnable{
	private final int port;
	private ServerSocketChannel ssc;
	private Selector selector;
	private ByteBuffer buf = ByteBuffer.allocate(256);
	private Game mainGame;

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
						if(mainGame.getPlayers().size()<4) this.handleAccept(key);
					if (key.isReadable())
						this.handleRead(key);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
			e.printStackTrace();
		}
	}

	private void handleAccept(SelectionKey key) throws IOException {
		SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
		String address = (new StringBuilder(sc.socket().getInetAddress().toString())).append(":")
				.append(sc.socket().getPort()).toString();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ, address);
		System.out.println("accepted connection from: " + address);
		broadcast();
	}

	private void handleRead(SelectionKey key) throws IOException {
		SocketChannel ch = (SocketChannel) key.channel();
		StringBuilder sb = new StringBuilder();

		buf.clear();
		while ((ch.read(buf)) > 0) {
			buf.flip();
			byte[] bytes = new byte[buf.limit()];
			buf.get(bytes);
			sb.append(new String(bytes));
			if (sb.toString().substring(0, 3).compareTo("map") == 0) {
				this.mainGame.setMap(
						Integer.parseInt(sb.toString().substring(sb.toString().length() - 1, sb.toString().length())));
				this.mainGame.setGameStarted(true);
				placePlayer();
			}else if(sb.toString().substring(0, 4).compareTo("CMD:") == 0) {
				System.out.println(sb.toString().substring(5, sb.toString().length()));
				if(sb.toString().substring(5, sb.toString().length()).compareTo("SERVERQUIT") == 0) {
					System.out.println("CLOSE SERVER");
					ssc.close();
				} else {
				this.mainGame.doCmd(Integer.parseInt(sb.toString().substring(4,5)), sb.toString().substring(5, sb.toString().length()));
				if (sb.toString().substring(5, sb.toString().length()).compareTo("SPACE")==0){
					Timer timer = new Timer();
				    timer.schedule(new MyTimerTask( mainGame.getListBomb().size()-1 ), 3000);
				}
				}
			} else {
				this.mainGame.getPlayers().add(new Player(sb.toString()));
			}
			buf.clear();
		}
		broadcast();
	}

	private void placePlayer() {
		for (int i = 0; i < mainGame.getPlayers().size(); i++)
			this.mainGame.getPlayers().get(i).spawn(i, mainGame.getMap().getHeight());
		this.mainGame.getMap().removeSpawn();
	}

	public void broadcast() throws IOException {
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
	
	class MyTimerTask extends TimerTask  {
	     int index;

	     public MyTimerTask(int param) {
	         this.index = param;
	     }

	     @Override
	     public void run() {
	    	 System.out.println("boomm + index :"+index);
	    	 explodeBombe(index);
	    	 this.cancel();
	     }
	}
	
	public void explodeBombe(int index) {
		// TODO Auto-generated method stub
		mainGame.explode(index);
		
		try {
			broadcast();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
