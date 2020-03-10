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
	private Player player;
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

			// BufferedReader that connects to the socket of this thread
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			// DataOutPutStream that sends the initial setup to client
			DataOutputStream initClientOutput = new DataOutputStream(connectionSocket.getOutputStream());

			String userName = inFromClient.readLine();
			player = new Player(userName, 1, 1, 0, "up");

			// TODO implement array of valid starting places for a given map. Property of
			// ServerMap class?
			// Check if player or wall already there

			// Send dimensions of board to client
			initClientOutput.writeBytes(serverMap.getDimensions() + " \n");
			// send board info as String to client
			for (String s : serverMap.getBoard()) {
				initClientOutput.writeBytes(s + "\n");
			}

			// TODO test for ReadThread BufferedReader ready?

			// Send newly joined player's info to all clients
			for (ClientInputThread ct : clients) {
				DataOutputStream outToCT = new DataOutputStream(ct.getConnectionSocket().getOutputStream());
				outToCT.writeBytes("SPAWN " + player.getStringStream());

				// send already connected players' info to client
				if (ct != this) {
					initClientOutput.writeBytes("SPAWN " + ct.getPlayer().getStringStream());
				}
			}

			boolean gameFinished = false;

			while (!gameFinished) {
				// Splits the input from the client in a String[] and parses it as needed format
				String[] formattedInput = inFromClient.readLine().split(" "); // int, int, String
				// The horizontal requested movement of the player.
				int deltaX = Integer.parseInt(formattedInput[0]);
				// The vertical requested movement of the player
				int deltaY = Integer.parseInt(formattedInput[1]);
				String direction = formattedInput[2];

				// Update the player of this thread
				playerMoved(deltaX, deltaY, direction);

				// Notify other clients of updated player
				for (ClientInputThread ct : clients) {
					DataOutputStream outToCT = new DataOutputStream(ct.getConnectionSocket().getOutputStream());
					outToCT.writeBytes("MOVE " + player.getStringStream());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that mutates the player's values based on input from client
	 * 
	 * @param delta_x   the amount the player is to be moved in the horizontal
	 *                  direction
	 * @param delta_y   the amount the player is to be moved in the vertical
	 *                  direction
	 * @param direction the way the player should be facing after being moved
	 */
	public synchronized void playerMoved(int delta_x, int delta_y, String direction) {
		System.out.println("Modtaget input i playerMoved()");
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

	/**
	 * Method that returns a player at a given coordinate
	 * 
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

	// Method that returns the player object of this thread;
	public Player getPlayer() {
		return player;
	}

	// Return the connectionSocket of this thread. For use in communication with
	// other threads only
	public Socket getConnectionSocket() {
		return connectionSocket;
	}
}
