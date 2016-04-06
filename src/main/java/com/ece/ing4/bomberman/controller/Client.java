package com.ece.ing4.bomberman.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
 
public class Client
{

    private static Socket socket;
 
    public static void main(String[] args) throws IOException
    {
    	
    	ThreadClient tc = new ThreadClient("localhost", 25000);
    	ThreadClient tc1 = new ThreadClient("localhost", 25000);
    	ThreadClient tc2 = new ThreadClient("localhost", 25000);
    }
}