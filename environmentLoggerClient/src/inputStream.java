/* Program By Adam Cornfield */

/*
 * Handles all data from the server and displays to console
 */

import java.net.*;
import java.io.*;

public class inputStream implements Runnable {
    private Socket client;

    public inputStream(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            //Creates input stream
            InputStream dataFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(dataFromServer);

            //Creates a while loop so it will constantly check for new data from the server
            while (true) {
                String inputData = in.readUTF();
                //switch statement detects if its a local command or data to be sent, switch statement keeps it efficient with minimal blocking
                switch (inputData) {
                    //The server can send a special command "--clearCMD", this will cause the client to clear the command line, for mainly aestetic reasons
                    case "--clearCMD":
                        try {
                            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                
                    default:
                        System.out.println(inputData);
                        break;
                }
            }
        } catch (SocketException se) {
            System.out.println("Socket Closed");
        } catch (IOException e) {
        }
    }
}
