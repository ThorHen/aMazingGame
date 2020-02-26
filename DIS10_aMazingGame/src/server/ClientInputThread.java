package server;

import java.net.Socket;
import java.util.ArrayList;

import javafx.scene.image.ImageView;

public class ClientInputThread extends Thread {
	
	private ArrayList<Socket> players;
	private Player player = new Player("Test", 0, 0, "up");

	public ClientInputThread(ArrayList<Socket> players) {
		super();
		this.players = players;
	}
	
	@Override
	public void run() {
		//TODO send player info as String?
		//Generate board
		
		boolean gameFinished = false;
		
		while(!gameFinished) {
			//send other player's position
			//update points
		}
	}
	
	public synchronized void playerMoved(Player p, int delta_x, int delta_y, String direction) {
		p.direction = direction;
		int x = p.getXpos(), y = p.getYpos();

		if (board[y + delta_y].charAt(x + delta_x) == 'w') {
			p.addPoints(-1);
		} else {
			Player p = getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				p.addPoints(10);
				p.addPoints(-10);
			} else {
				p.addPoints(1);

				fields[x][y].setGraphic(new ImageView(image_floor));
				x += delta_x;
				y += delta_y;

				p.setXpos(x);
				p.setYpos(y);
			}
		}
		
		if (direction.equals("right")) {
			fields[x][y].setGraphic(new ImageView(hero_right));
		}
		if (direction.equals("left")) {
			fields[x][y].setGraphic(new ImageView(hero_left));
		}
		if (direction.equals("up")) {
			fields[x][y].setGraphic(new ImageView(hero_up));
		}
		if (direction.equals("down")) {
			fields[x][y].setGraphic(new ImageView(hero_down));
		}
		
		scoreList.setText(getScoreList());
	}
	
	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		return b.toString();
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

}
