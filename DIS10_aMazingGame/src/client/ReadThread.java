package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import server.Player;

public class ReadThread extends Thread {

	public static final int size = 20;

	public static Image image_floor;
	public static Image hero_right, hero_left, hero_up, hero_down;

	private Socket connectionSocket;
	private Label[][] fields;
	private List<Player> players = new ArrayList<Player>();
	private TextArea scoreList;

	public ReadThread(Socket connectionSocket, Label[][] fields, TextArea scoreList) {
		super();
		this.connectionSocket = connectionSocket;
		this.fields = fields;
		this.scoreList = scoreList;
	}

	public void run() {
		try {
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

			hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
			hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
			hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
			hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			boolean finished = false;
			while (!finished) {
				if (connectionSocket.isClosed()) {
					finished = true;
				} else {
					String[] playerChange = inFromServer.readLine().split(" ");
					//System.out.println(Arrays.toString(playerChange));

					if (playerChange[0].equals("SPAWN")) {
						// Spawn changes
						int xpos = Integer.parseInt(playerChange[2]);
						int ypos = Integer.parseInt(playerChange[3]);
						int point = Integer.parseInt(playerChange[4]);

						Player spawnedPlayer = new Player(playerChange[1], xpos, ypos, point, playerChange[5]);
						players.add(spawnedPlayer);

						// Update GUI elements
						Platform.runLater(() -> {
							fields[xpos][ypos].setGraphic(new ImageView(hero_up));
						});

					} else if (playerChange[0].equals("MOVE")) {
						boolean found = false;
						int i = 0;

						while (!found && i < players.size()) {
							Player currentPlayer = players.get(i);
							if (currentPlayer.getName().equals(playerChange[1])) {
								found = true;

								int oldXPos = currentPlayer.getXpos();
								int oldYPos = currentPlayer.getYpos();

								// set old position graphic to floor

								int xpos = Integer.parseInt(playerChange[2]);
								int ypos = Integer.parseInt(playerChange[3]);
								int point = Integer.parseInt(playerChange[4]);

								Player updatedPlayer = new Player(playerChange[1], xpos, ypos, point, playerChange[5]);

								players.set(i, updatedPlayer);

								Platform.runLater(() -> {
									fields[oldXPos][oldYPos].setGraphic(new ImageView(image_floor));

									if (playerChange[5].equals("right")) {
										fields[xpos][ypos].setGraphic(new ImageView(hero_right));
									}
									if (playerChange[5].equals("left")) {
										fields[xpos][ypos].setGraphic(new ImageView(hero_left));
									}
									if (playerChange[5].equals("up")) {
										fields[xpos][ypos].setGraphic(new ImageView(hero_up));
									}
									if (playerChange[5].equals("down")) {
										fields[xpos][ypos].setGraphic(new ImageView(hero_down));
									}
								});
							}

							i++;
						}
					}
					Platform.runLater(() -> {
						scoreList.setText(getScoreList());
					});
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		return b.toString();
	}
}