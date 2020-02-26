package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ClientInputThread extends Thread {
	
	private Socket connectionSocket;
	private ArrayList<ClientInputThread> clients;
	private Player player = new Player("Test", 0, 0, "up");
	private ServerMap serverMap;

	public ClientInputThread(Socket connectionSocket, ArrayList<ClientInputThread> clients, ServerMap serverMap) {
		super();
		this.clients = clients;
		this.serverMap = serverMap;
		this.connectionSocket = connectionSocket;
	}
	
	@Override
	public void run() {
		try {
			
			//BufferedReader that connects to the socket of this thread
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			//DataOutPutStream that sends the initial setup to client
			DataOutputStream initClientOutput = new DataOutputStream(connectionSocket.getOutputStream());
			
			//TODO implement String username = inFromClient.readLine() -> client's wanted userName
			//player = new Player("userName", 0, 0, "up")
			
			//send board info as String to client
			for(String s :serverMap.getBoard()) {
				initClientOutput.writeBytes(s);
			}
			

			//send already connected players' info to client
			for(ClientInputThread ct : clients) {
				initClientOutput.writeBytes(ct.getPlayer().getStringStream());
			}
			
			boolean gameFinished = false;
			
			while(!gameFinished) {
				//Splits the input from the client in a String[] and parses it as needed format
				String[] formattedInput = inFromClient.readLine().split(" "); //[int, int, String]
				int deltaX = Integer.parseInt(formattedInput[0]);
				int deltaY = Integer.parseInt(formattedInput[1]);
				String direction = formattedInput[2];
				
				//Update the player of this thread
				playerMoved(deltaX, deltaY, direction);
				
				//Notify other clients of updated player
				for(ClientInputThread ct : clients) {
					DataOutputStream outToCT = new DataOutputStream(ct.getConnectionSocket().getOutputStream());
					outToCT.writeBytes(player.getStringStream());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that mutates the player's values based on input from client
	 * @param delta_x the amount the player is to be moved in the horizontal direction
	 * @param delta_y the amount the player is to be moved in the vertical direction
	 * @param direction the way the player should be facing after being moved
	 */
	public synchronized void playerMoved(int delta_x, int delta_y, String direction) {
		player.direction = direction;
		int x = player.getXpos(), y = player.getYpos();

		if (serverMap.getBoard()[y + delta_y].charAt(x + delta_x) == 'w') {
			player.addPoints(-1);
		} else {
			Player p = getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				player.addPoints(10);
				p.addPoints(-10);
			} else {
				player.addPoints(1);

				x += delta_x;
				y += delta_y;

				player.setXpos(x);
				player.setYpos(y);
			}
		}
	}
	
	//TODO move this method to client side
	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (ClientInputThread ct : clients) {
			Player p = ct.getPlayer();
			b.append(p + "\r\n");
		}
		return b.toString();
	}
	
	/**
	 * Method that returns a player at a given coordinate
	 * @param x 
	 * @param y
	 * @return the player at a given coordinate, if one exists, otherwise null
	 */
	public Player getPlayerAt(int x, int y) {
		for (ClientInputThread ct : clients) {
			Player p = ct.getPlayer();
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

	//Method that returns the player object of this thread;
	public Player getPlayer() {
		return player;
	}
	
	//Return the connectionSocket of this thread. For use in communication with other threads only
	public Socket getConnectionSocket() {
		return connectionSocket;
	}
}
