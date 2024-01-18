/* Program By Adam Cornfield */

/*
 * This is another class that stores a list of all currently connected clients/Threads, when a new client connects or disconnects this central list is updated.
 * The list is an ArrayList of the Client class and contains all of the client objects as they are created
 */

import java.util.ArrayList;

public class ClientThreads {
    private static ArrayList<Client> list = new ArrayList<Client>();

    //Returns a list of all currently connected clients, returns the client objects
    public static ArrayList<Client> listClients() {
        return list;
    }

    //Returns an integer value for number of connected clients
    public static int clientsNo() {
        return list.size();
    }

    //Add a client object to the ArrayList
    public static void addClient (Client client) {
        list.add(client);
    }

    //Get a client from the ArrayList via an index
    public static Client getClient(int index) {
        Client client = list.get(index);

        return client;
    }

    //Takes a Client object and removes it from the ArrayList
    public static void removeClient(Client client) {
        list.remove(client);
    }

    //Takes an index and removes it from the ArrayList
    public static void removeClient(int index) {
        list.remove(index);
    }
}
