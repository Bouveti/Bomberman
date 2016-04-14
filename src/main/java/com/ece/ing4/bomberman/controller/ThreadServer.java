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
				this.mainGame.setMap(Integer.parseInt(sb.toString().substring(sb.toString().length()-1, sb.toString().length())));
				this.mainGame.setGameStarted(true);
				placePlayer();
			}else if(sb.toString().substring(0, 4).compareTo("CMD:") == 0) {
				this.mainGame.movePlayer(Integer.parseInt(sb.toString().substring(4,5)), sb.toString().substring(5, sb.toString().length()));
			}else {
				this.mainGame.getPlayers().add(new Player(sb.toString()));
			}
			buf.clear();
		}
		broadcast();
	}

	private void placePlayer() {
		for(int i = 0;i<mainGame.getPlayers().size();i++)
		this.mainGame.getPlayers().get(i).spawn(i, mainGame.getMap().getHeight());
		this.mainGame.getMap().removeSpawn();
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
