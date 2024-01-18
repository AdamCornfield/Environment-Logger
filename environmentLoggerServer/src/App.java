/* Program By Adam Cornfield */

/*
 * Program is for a university project, CIS 5027 WRIT1 Object-Oriented System Design.
 * Will take a connection from up to 4 clients and allow them to upload data
 * Includes fully functional serverside commandline interface
 * 
 * Main class for the server program, initiates all threads and sub-processes
 */

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class App {
    public static void main(String[] args) throws Exception {
        //Start of the server program
        int port = 6602;

        //Crates a new thread which the server will be created on
        //This thread will be dedicated towards handling any incoming connection requests
        try {
            Thread t = new Server(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Activate commandHandler System
        commandHandler();
    }

    //This command handler runs on the main thread
    private static void commandHandler() {
        //Loads all commands into memory
        Command command = new Command();

        while (true) {
            Scanner myObj = new Scanner(System.in);
            String commandInput = myObj.nextLine();

            // Once a command comes in it will split it into an array to get the arguments
            String[] splitCommand = commandInput.split(" ");
            String commandName = splitCommand[0];

            //Get rid of the invoker command on the left to leave you with just the arguments
            util.shiftLeft(splitCommand);

            try {
                //Selects the specific method based from the input
                Method method = Command.class.getMethod(commandName, String[].class);

                Object[] arguments = {splitCommand};

                //Invokes the method and passes in an array of arguments (arguments are optional)
                System.out.println(method.invoke(command, arguments));
            } catch (NoSuchMethodException e) {
                //Catch route in case an incorrect command is entered
                System.out.println('"' + commandName + '"' + " is not a recognised command.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
