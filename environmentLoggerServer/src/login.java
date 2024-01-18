/* Program By Adam Cornfield */

/*
 * Contains all login and authentication methods for the server
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class login {
    public static String checkCred(String user, String passw) {
        try {
            File file = new File("src\\data\\logins.txt");
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                //Saves data to local variables
                String data = myReader.nextLine();
                String userID = data.split("\\~")[0];
                String storedName = data.split("\\~")[1];
                String storedPassw = CSR.Decrypt(data.split("\\~")[2], 5);
                String perms = data.split("\\~")[3];

                //If the input username and password matches both entries in the login file it will authorise the user
                if (storedName.equals(user) && storedPassw.equals(passw)) {
                    //Determines where the user should go based on their access level.
                    myReader.close();
                    return userID + "~" + storedName + "~" + perms;
                } else {
                    //Error respose for incorrect details
                }
            }
            myReader.close();
            return "0~~invalid";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "0~~err";
        }
    }

    public static String createUser(String user, String passw, String perms) {
        try {
            // This code gets all of the userIDs and gets the next one in the chain
            //This is an auto incrementing system
            Path path = Paths.get("src\\data\\logins.txt");
            List<String> lines = Files.readAllLines(path);
            int userID = Integer.parseInt(lines.get(lines.size() - 1).split("~")[0]);

            //Gets all of the data from the parameters and uses the append to file method to save it.
            fileSystem.appendToFile("src\\data\\logins.txt", (userID + 1) + "~" + user + "~" + CSR.Encrypt(passw, 5) + "~" + perms);

            return "created";
        } catch (IOException e) {
            e.printStackTrace();

            return "Error during account creation";
        }
    }
}
