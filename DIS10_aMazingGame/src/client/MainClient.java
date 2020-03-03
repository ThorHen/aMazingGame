package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class MainClient extends Application {

	//Size of squares
	public static final int size = 20;
	//square size * nr of squares vertically + buffer
	public static final int scene_height = size * 20 + 100;
	//square size * nr of squares horizontally + scoreboard buffer
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right, hero_left, hero_up, hero_down;

	private Label[][] fields;
	private TextArea scoreList;
	private String[] board;

	// -------------------------------------------
	// | Maze: (0,0) | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1) | scorelist |
	// | | (1,1) |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			//INIT START
			
			//Connection socket to the server
			Socket clientSocket = new Socket("localhost", 6789);
			
			//BufferedReader that connects to the connection socket
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			//DataOutPutStream that sends data to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			
			//TODO choose userName and send to server
			System.out.println("Choose a username...");
			
			//INPUT USERNAME
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			outToServer.writeBytes(input.readLine() + "\n");
			input.close();

			//LISTEN FOR SERVER MAP
			String[] dimensions = inFromServer.readLine().split(" ");
			int widthOfBoard = Integer.parseInt(dimensions[0]);
			int heightOfBoard = Integer.parseInt(dimensions[1]);
			
			String[] inputBoard = new String[heightOfBoard];
			
			for(int i = 0; i < heightOfBoard; i++) {
				inputBoard[i] = inFromServer.readLine();
			}
			
			//inFromServer.close();
			
			board = inputBoard;
			
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();

			GridPane boardGrid = new GridPane();

			image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

			hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
			hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
			hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
			hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

			fields = new Label[widthOfBoard][heightOfBoard];
			for (int j = 0; j < heightOfBoard; j++) {
				for (int i = 0; i < widthOfBoard; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default:
						throw new Exception("Illegal field value: " + board[j].charAt(i));
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			
			//TODO make ReadThread that listens for server updates
			//ReadThread has responsibility for updating fields
			
			ReadThread rt = new ReadThread(clientSocket, fields, scoreList);
			rt.start();
			scoreList.setEditable(false);

			grid.add(mazeLabel, 0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid, 0, 1);
			grid.add(scoreList, 1, 1);

			Scene scene = new Scene(grid, scene_width, scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();
			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:
//					playerMoved(0, -1, "up");
					try {
						System.out.println("Modtaget input i client");
						outToServer.writeBytes("0 -1 up \n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case DOWN:
//					playerMoved(0, +1, "down");
					try {
						outToServer.writeBytes("0 1 down \n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case LEFT:
//					playerMoved(-1, 0, "left");
					try {
						outToServer.writeBytes("-1 0 left  \n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case RIGHT:
//					playerMoved(+1, 0, "right");
					try {
						outToServer.writeBytes("1 0 right \n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
