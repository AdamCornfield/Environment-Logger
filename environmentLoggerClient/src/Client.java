/* Program By Adam Cornfield */

/*
 * Program is for a university project, CIS 5027 WRIT1 Object-Oriented System Design.
 * Client companion to the environment data storage system
 * 
 * Universal client system means that this client once connected, responds to the demands of the server, so any changes on the server does not nessecarily require a change in client code.
 */

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {
    public static void main(String[] args) throws Exception {
        //Defines default connection parameters
        String serverName = "localhost";
        int port = 6602;
        Scanner inputScanner = new Scanner(System.in);

        //Checks if any parameters have been passed to the program at start
        //If there are then it will use those and start the program, if not then it will ask for the data
        if (args.length == 2) {
            serverName = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            //I have included default values, so the user can either enter the data or just press enter to use the defaults
            System.out.println("Please enter the ip address and port to connect to the server");
            System.out.println("Example : 'localhost:6602'");
            System.out.println("Example : 'press enter for default operation'");
    
            String connStr = inputScanner.nextLine();

            //Detects if the user has just pressed enter or entered data
            if (!connStr.isBlank()) {
                serverName = connStr.split(":")[0];
                port = Integer.parseInt(connStr.split(":")[1]);
            }
        }


        try {
            //Initiates connection to the server
            System.out.println("Connecting to " + serverName + " on port " + port + "...");
            Socket client = new Socket(serverName, port);
            System.out.println("Connected");
    

            //Creates a separate thread to display all data from server
            //Since this is running on it's own thread, no matter what the user is doing, it will always display the most recent data that is coming from the server
            inputStream stream = new inputStream(client);
            new Thread(stream).start();

            //Setup the output data to the server
            OutputStream dataToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(dataToServer);

            //Loops to constantly take an input, uses labeling technique to break the while look from within a switch case.
            //Since it is on a different thread to the data input, you can send data whenever you want, whether the server reads that data however isn't nessecarily guaranteed
            outerLoop: while (true) {
                String inputText = inputScanner.nextLine();  // Read user input
                //I have included a command baked directly into the client, if they do "!stop" it will close the connection irregardless of what the server is doing
                switch (inputText) {
                    case "!stop":
                        break outerLoop;
                    default:
                        //Sends the data to the server
                        out.writeUTF(inputText);
                }
            }

            //Close socket connection
            inputScanner.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
