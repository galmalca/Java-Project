package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import view.ServerGui;
import view.View;

public class MyServer {
	
	int port;
	ClientHandler ch;
	ExecutorService threadPool;
	boolean terminated = true;
	int numberOfClients;
	ServerGui gui;
	
	public MyServer(int port,ClientHandler ch,View v) {
		numberOfClients = 0;
		this.port = port;
		this.ch = ch;
		threadPool = Executors.newFixedThreadPool(10);
		this.gui = (ServerGui) v;
	}
		

	
	public void startServer(){
		try {
			ServerSocket server = new ServerSocket(port);//tcp
			server.setSoTimeout(10000);
			while(terminated){
				try{
					final Socket someClient = server.accept();
					threadPool.execute(new Runnable() {
						
						@Override
						public void run() {
							try{
								System.out.println("client is alive");
								InputStream inFromClient = someClient.getInputStream();
								OutputStream outToClient = someClient.getOutputStream();	
								numberOfClients++;
								gui.addClient(someClient);
								gui.countClient(numberOfClients);
								ch.handleClient(inFromClient, outToClient);							
								someClient.close();
								numberOfClients--;
								gui.countClient(numberOfClients);
								gui.removeClient(someClient);
							}catch (IOException e){
								e.printStackTrace();
							}
							
						}
					});
				}catch(IOException e){
					//System.out.println("w8ing for client");
				}
			}	
				server.close();
			}catch (IOException e)
			{
				e.printStackTrace();
			} 
	}
	
	public void start() 
	{
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			startServer();
			
		}
	}).start();
	
	}
	public void stop()
	{
		terminated = false;
		threadPool.shutdown();

		
	}
	

	
}
