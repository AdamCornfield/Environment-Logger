/* Program By Adam Cornfield */

/*
 * This class stores all of the commands, invoke is used to dynamically call each method.
 * Methods are designed to return all data generated to allow for modularity of where output data is sent.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Command {
    /*
     * Client Management
     */

    // Lists all connected clients, along with their client number, IP Address, username and permission group
    public String list(String[] args) {
        int connections = ClientThreads.listClients().size();
        String outputString = "";

        outputString = outputString + ("\nThere are currently " + connections + " client(s) connected:") + "\n";

        for (int i = 0; i < connections; i++) {
            Client client = ClientThreads.listClients().get(i);

            outputString = outputString + ("Client " + i + ": " + client.getIP() + " " + client.getUsername() + ":" + client.getUserPerms()) + "\n";
        }

        return outputString;
    }

    //Takes an IP Address and forcibly disconnects it from the server
    public void kick (String[] args) {
        for (Client element : ClientThreads.listClients()) {
            if (args[0].equals(element.getIP())) {
                element.kick();
            }
        }
    }

    /*
     * Encryption Tools
     * Utility only, helps for debugging and decrypting and encrypting the passwords used
     */

    public String csrEncrypt(String[] args) {
        return CSR.Encrypt(args[0], Integer.parseInt(args[1]));
    }

    public String csrDecrypt(String[] args) {
        return CSR.Decrypt(args[0], Integer.parseInt(args[1]));
    }

    /*
     * user management
     */
    
    //Creates a new user account and adds it to the logins file
    public String useradd(String[] args) {
        return login.createUser(args[0], args[1], args[2]);
    }

    //Lists all of the currently created user accounts
    public String userlist(String[] args) {
        try {
            File file = new File("src\\data\\logins.txt");
            Scanner myReader = new Scanner(file);
        
            String outputData = "User ID | Username | Group\n";

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] row = data.split("~");

                outputData = outputData + row[0] + "  |  "  + row[1] + "  |  "  + row[3] + "\n";
            }

            myReader.close();
            return outputData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /*
     * miscelaneous
     */

    //Writes out all current commands for the user
    public String help(String[] args) {
        return "\nBelow are the available commands: \n - list         List all currently connected clients\n - kick         Kick a client from the server, uses IP Address\n\n - csrEncrypt   Takes 2 parameters, text to be encrypted and the shift value\n - csrDecrypt   Takes 2 parameters, text to be decrypted and the shift value\n\n - useradd      Creates a new user on the system, takes 3 parameters, username, password and either ADMIN or user depending on required access.\n - userlist     Lists out all users along with their ID, Name and Permissions Group\n\n - help         Lists all usable commands\n - unlock       Unlocks the database from read only mode to make it editable again.";
    }

    //In case the database gets stuck in locked mode due to an unexpected client crack, it can be forcibly unlocked from the server
    public String unlock(String[] args) {
        File file = new File("src/data/envData.csv");
        file.setWritable(true);

        return "Database unlocked";
    }
}