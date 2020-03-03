package server;

public class ServerMap {

	//The game board. w = wall, space = moving area
	private String[] board = { // 20x20
			"wwwwwwwwwwwwwwwwwwww", 
			"w        ww        w", 
			"w w  w  www w  w  ww", 
			"w w  w   ww w  w  ww",
			"w  w               w", 
			"w w w w w w w  w  ww", 
			"w w     www w  w  ww", 
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w", 
			"w     w  w  w  w   w", 
			"w ww ww        w  ww", 
			"w  w w    w    w  ww",
			"w        ww w  w  ww", 
			"w         w w  w  ww", 
			"w        w     w  ww", 
			"w  w              ww",
			"w  w www  w w  ww ww", 
			"w w      ww w     ww", 
			"w   w   ww  w      w", 
			"wwwwwwwwwwwwwwwwwwww" };

	public ServerMap() {
	}

	
	public String[] getBoard() {
		return board;
	}
	
	public String getDimensions() {
		return board[0].length() + " " + board.length;
	}
}
