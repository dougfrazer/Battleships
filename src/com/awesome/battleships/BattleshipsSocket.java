package com.awesome.battleships;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class BattleshipsSocket {
	
	public class ClientThread extends Thread {
	    /** For reading input from socket */
	    private BufferedReader br;

	    /** For writing output to socket. */
	    private PrintWriter pw;

	    /** Socket object representing client connection */
	    private Socket socket;
	    private boolean running;
	    
	    public ClientThread(Socket s) throws IOException {
	        this.socket = s;
	        running = false;
	        try {
	            br = new BufferedReader(
	                   new InputStreamReader(socket.getInputStream()));
	            
	            pw = new PrintWriter(socket.getOutputStream(), true);
	            running = true;
	        } catch (IOException ioe) {
	            throw ioe;
	        }
	    }
	    
	    public void writeToSocket(String s) {
	    	pw.println(s);
	    }
	    
		public void run() {
	        String msg = "";
	        while(running) {
	            try {
	            	while ((msg = br.readLine()) != null && running) {
	            		parseLogic(msg); 
	            	}
	            } catch (IOException ioe) {}
	        }
		}
		
		public void closeSocket() {
            try {
            	this.socket.close();
            	System.out.println("Closing connection");
            } catch (IOException ioe) { }
		}
		
		public void setRunning(boolean b) {
			running = b;
		}

		private void parseLogic(String msg) {
			
			if (msg.equalsIgnoreCase("ACK")) {
				// process ACKs
				return;
			}
			if (msg.equalsIgnoreCase("MOVE")) {
				// process move command
				return;
			}
		}
		
	}
	private final static int SERVER_PORT = 12345;
	private ClientThread thread;
	
	public BattleshipsSocket() throws UnknownHostException, IOException, SecurityException {
		new BattleshipsSocket("a", "a", "a");
	}
	
	public BattleshipsSocket(String username, String password, String server) throws UnknownHostException,
																					 IOException,
																					 SecurityException {
		Socket s;
		s = new Socket("scottio.us", 12345);
		if (s != null)
			Log.d("BattleshipsSocket", "Successfully opened socket");
		thread = new ClientThread(s);
		thread.start();
	}
	
	public void write(String s) {
		thread.writeToSocket(s);
	}

}
