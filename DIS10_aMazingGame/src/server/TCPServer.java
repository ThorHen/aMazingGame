package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


public class TCPServer {
    public static void main(String[] args) throws Exception {

        ServerSocket welcomSocket = new ServerSocket(6789);
     
        //ArrayList of connected clients
        ArrayList<ClientInputThread> clients = new ArrayList<>();
        
        //The server map for all clients to use
        ServerMap sm = new ServerMap();

        while (true) {
            Socket connectionSocket = welcomSocket.accept();

            //When a client connects to the server -> create new thread that handles input from client
            ClientInputThread ct = new ClientInputThread(connectionSocket, clients, sm);
            clients.add(ct);
            ct.start();
        }
    }
}
