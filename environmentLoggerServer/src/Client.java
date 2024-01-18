/* Program By Adam Cornfield */

/*
 * Client class is able to be ran on independant threads upon creation, it has various different public facing methods and is designed to an object-oriented specification.
 * Most interaction with the client is stored here and all program instructions to operate the data storage is also kept here.
 */

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client implements Runnable {
    /*
     * Defines all of the global variables for each client
     */
    private Socket socket;
    private int userID;
    private String username;
    private String perms;
    private Thread thread = Thread.currentThread();
    private DataOutputStream out;
    private DataInputStream in;

    /*
     * Defines all of the publically available methods
     */

    public Client(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public String getIP() {
        return socket.getRemoteSocketAddress().toString().replaceAll("/", "");
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int data) {
        userID = data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String data) {
        username = data;
    }

    public String getUserPerms() {
        return perms;
    }

    public void setUserPerms(String data) {
        perms = data;
    }

    public Thread getThread() {
        return thread;
    }

    public void kick() {
        try {
            out.writeUTF("Your connection has been terminated.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Client code that is ran once they have connected, this is the main entry to this class
    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            //Checks how many clients are connected and if the max has been reached, if it has the user is disconnected
            if (ClientThreads.clientsNo() > 4) {
                out.writeUTF("Max number of clients reached (4), please try again later");
                socket.close();
                ClientThreads.removeClient(this);
            } else {
                System.out.println("Connection approved for " + socket.getRemoteSocketAddress());

                //Send user to the login menu
                login();
            }

            
            socket.close();
            ClientThreads.removeClient(this);          

        } catch (SocketTimeoutException s) {
            ClientThreads.removeClient(this);
            System.out.println("Socket timed out!");
        } catch (EOFException e) {
            ClientThreads.removeClient(this);
            System.out.println("Socket closed by client.");
        } catch (IOException e) {
            ClientThreads.removeClient(this);
        }
    }

    private void login () throws IOException {
        out.writeUTF("--clearCMD");
        out.writeUTF("Welcome to the Environmental CO2 System");

        //Reset user data to defaults
        setUserID(0);
        setUsername(null);
        setUserPerms(null);

        while (true) {
            //Menu chain for login page
            out.writeUTF("Please provide your username:");
            String user = in.readUTF();
    
            out.writeUTF("Please provide your password:");
            String passw = in.readUTF();

            String[] result = login.checkCred(user, passw).split("~");

            //uses public methods to set the client data
            setUserID(Integer.parseInt(result[0]));
            setUsername(result[1]);
            setUserPerms(result[2]);
            

            switch (perms) {
                case "ADMIN":
                    out.writeUTF("--clearCMD");
                    out.writeUTF("Login successful!\n");
                    adminRoute();
                    break;
                case "USER":
                    out.writeUTF("--clearCMD");
                    out.writeUTF("Login successful!\n");
                    userRoute();
                    break;
                case "invalid":
                    out.writeUTF("--clearCMD");
                    out.writeUTF("Incorrect credentials, please try again \n");
                    break;
                case "err":
                    out.writeUTF("An error has occured, please try again later");
                    break;
                default:
            }
        }
    }

    /*
     * Special mode if the user is detected to be in the admin group
     * Allows access to the server-side command console as if you were directly on the server
     * Allows for creation of new user accounts and to manage connected clients and more
     */
    private void adminRoute () throws IOException {
        out.writeUTF("--clearCMD");
        out.writeUTF("Welcome to the admin command console!");
        out.writeUTF("Use command 'logout' to return to the login menu.");
        Command command = new Command();

        while (true) {
            String commandInput = in.readUTF();
            
            if (commandInput.equals("logout")) {
                out.writeUTF("Logging out of command console");
                login();
            } else {
                String[] splitCommand = commandInput.split(" ");
                
                String commandName = splitCommand[0];
    
                util.shiftLeft(splitCommand);
    
                try {
                    //Selects the specific method based from the input
                    Method method = Command.class.getMethod(commandName, String[].class);
    
                    Object[] arguments = {splitCommand};
    
                    //Invokes the method
                    out.writeUTF((String) method.invoke(command, arguments));
                } catch (NoSuchMethodException e) {
                    out.writeUTF('"' + commandName + '"' + " is not a recognised command.");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Standard route for most users
     * Gives them 5 options of things they can do
     * It is set up so they can either type a number or the option they want to select
     */
    private void userRoute () throws IOException {
        while (true) {
            out.writeUTF("--clearCMD");
            out.writeUTF("Welcome to the Environmental CO2 System");
            out.writeUTF("Please type one of the following options to continue: \n 1 - upload      Interface to upload environmental readings\n 2 - unlock      Forcibly unlock the database NOTICE: Only use if you are sure no one else is connected to prevent data corruption.\n 3 - view        View all of the currently submitted data\n 4 - logout      Logout and return to the login menu\n 5 - disconnect  Terminate connection with server");
            String menuOption = in.readUTF();
            switch (menuOption) {
                case "upload":
                    uploadRoute();
                    break;
                case "1":
                    uploadRoute();
                    break;
                case "unlock":
                    unlockRoute();
                    break;
                case "2":
                    unlockRoute();
                    break;
                case "view":
                    viewRoute();
                    break;
                case "3":
                    viewRoute();
                    break;
                case "logout":
                    login();
                    break;
                case "4":
                    login();
                    break;
                case "disconnect":
                    out.writeUTF("Connection with server terminated, have a nice day");
                    socket.close();
                    break;
                case "5":
                    out.writeUTF("Connection with server terminated, have a nice day");
                    socket.close();
                    break;
                default:
                    out.writeUTF("--clearCMD");
                    out.writeUTF("That is not an option, please try again.\n");
                    break;
            }
        }
    }

    /*
     * Will collect all data with full data validation and sanitation and submit it to the server
     * locks the file whenever it is opened, so if it is not unlocked then no one else can edit the file
     */
    private void uploadRoute () throws IOException {
        out.writeUTF("--clearCMD");

        //Checks if file is editable, if it is then it locks it and starts editing, if not it tells the user to try again later
        File file = new File("src/data/envData.csv");
        if (!file.canWrite()) {
            out.writeUTF("Someone else is already editing this file, please wait until they have finished to try again.");
            out.writeUTF("\nPress enter to continue...");
            in.readUTF();
            userRoute();
        }
        file.setWritable(false);

        //Define variables
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime;
        int inpUserID;
        String postCode;
        Double concentration;

        /*
         * Date Collection
         * Uses java time library to format and validate the date time
         */

        out.writeUTF("Please enter the time the data was collected using the format (YYYY-MM-DD HH:MM:SS):");
        while (true) {
            try {
                String date = in.readUTF();

                localDateTime = LocalDateTime.parse(date, formatter);
                break;
            } catch (DateTimeParseException e) {
                out.writeUTF("Date is not of correct format, please try again");
            }
        }
        
        /*
         * Collects user ID
         * Uses a try catch loop and parse int to ensure it's a valid integer
         * It is intended that a user uses their own userID, however people can submit on others behalf
         */

        out.writeUTF("\nNow confirm your user ID");
        out.writeUTF("Your userID is " + userID);
        while (true) {
            try {
                inpUserID = Integer.parseInt(in.readUTF());
                break;
            } catch (NumberFormatException e) {
                out.writeUTF("That is not a correct userID, please try again");
            }
        }

        /*
         * Collects post code
         * Uses regex to validate the input
         */

        String postcodeRegex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$";
        Pattern pattern = Pattern.compile(postcodeRegex);

        out.writeUTF("\nNext please enter the post code of the data");
        out.writeUTF("example post code: 'CF5 2YB'");
        while (true) {
            postCode = in.readUTF().toUpperCase();

            if (pattern.matcher(postCode).matches()) {
                break;
            } else {
                out.writeUTF("That is not a valid UK post code, please try again");
            }
        }

        /*
         * Collect airquality information
         * Collects data as a double, uses a try catch to ensure it is the correct format and does not include any other data
         * I use an if statement to prevent the concentration from being a negative value as that is not possible
         */

        out.writeUTF("\nFinally, please enter the CO2 concentration in PPM (Parts Per Million)");
        while (true) {
            try {
                concentration = Double.parseDouble(in.readUTF());

                if (concentration >= 0) {
                    break;
                } else {
                    out.writeUTF("That is not a valid concentration value, please try again");
                }
            } catch (NumberFormatException e) {
                out.writeUTF("That is not a valid concentration value, please try again");
            }
        }

        // Pulls data from file as a 2D Array List, once data has been edited, it is the saved back to the csv file
        ArrayList<List<String>> envData = fileSystem.parseCSV("src/data/envData.csv");
        envData = fileSystem.addToList(envData, localDateTime.format(formatter), "" + inpUserID, postCode, Double.toString(concentration));
        fileSystem.listToCSV(envData, "src/data/envData.csv");

        //Make the file Writable again
        file.setWritable(true);

        out.writeUTF("Data has successfully been saved to the database");
        out.writeUTF("Press enter to continue...");
        in.readUTF();
    }

    /*
     * Will get all of the data from the database and list it out into a visible table for the user
     */

    private void viewRoute () throws IOException {
        out.writeUTF("--clearCMD");
        ArrayList<List<String>> envData = fileSystem.parseCSV("src/data/envData.csv");

        out.writeUTF("        Date        | ID | Post Code | CO2 Concentration");
        out.writeUTF("--------------------------------------------------------");
        for (List<String> row : envData) {
            out.writeUTF(row.get(0) + " | " + row.get(1) + " | " + row.get(2) + " | " + row.get(3) + "ppm");
        }

        out.writeUTF("\nPress enter to continue...");
        in.readUTF();
    }

    /*
     * Backup incase the file is locked with no one in it
     * It is unrestricted as everyone who has access is assumed to be trusted and should ask collegues before using this to ensure no one is actually in it
     */

    private void unlockRoute() throws IOException {
        File file = new File("src/data/envData.csv");

        out.writeUTF("--clearCMD");
        out.writeUTF("Are you sure you want to unlock this file? (Y/N)");

        String answer = in.readUTF().toUpperCase();
        switch (answer) {
            case "Y":
                file.setWritable(true);
                out.writeUTF("File unlocked.");
                break;
            case "YES":
                file.setWritable(true);
                out.writeUTF("File unlocked.");
                break;
            default:
                out.writeUTF("File not unlocked.");
                break;
        }

        out.writeUTF("\nPress enter to continue...");
        in.readUTF();
    }
}
