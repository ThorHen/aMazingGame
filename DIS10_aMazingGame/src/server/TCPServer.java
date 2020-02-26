package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class TCPServer {
    public static void main(String[] args) throws Exception {

        ServerSocket welcomSocket = new ServerSocket(6789);
        
        ArrayList<Socket> clients = new ArrayList<>();

        while (true) {
            Socket connectionSocket = welcomSocket.accept();
            clients.add(connectionSocket);
            ClientInputThread ct = new ClientInputThread(clients);
            
        }
    }
}
