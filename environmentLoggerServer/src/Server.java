/* Program By Adam Cornfield */

/*
 * Handles all of the connections and creates the individual threads for each client
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        util.clearCMD();
        System.out.println("Waiting for clients on port " + serverSocket.getLocalPort() + "...");
        System.out.println("Type 'help' for a list of available console commands");
        while(true) {
            try {

                //Accepts connections request
                Socket clientSocket = serverSocket.accept();

                //Creates new client object from client class
                Client client = new Client(clientSocket);

                //Add to client threads list
                ClientThreads.addClient(client);

                //Start thread
                new Thread(client).start();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
