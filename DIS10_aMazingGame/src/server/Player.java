package server;

public class Player {
	String name;
	int xpos;
	int ypos;
	int point;
	String direction;

	public Player(String name, int xpos, int ypos, int point, String direction) {
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.point = point;
		this.direction = direction;
	}
	
	public String getName() {
		return name;
	}
	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void addPoints(int p) {
		point+=p;
	}
	public String toString() {
		return name+":   "+point;
	}
	
	/**
	 * Return all of the player object's fields as a string. For use in client communication only
	 * @return String object containing all of the fields
	 */
	public String getStringStream() {
		return name + " " + xpos + " " + ypos + " " + point + " " + direction + " \n";
	}
}
