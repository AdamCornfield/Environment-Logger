/* Program By Adam Cornfield */

/*
 * Stores various utility functions that revolve around interacting with the windows file systsem and/or editing the 2D Array list to add, remove and edit data
 * By storing it in this separate file it keeps the code cleaner and more reusable
 */

import java.io.*;
import java.util.*;

public class fileSystem {
    //Used in the login system to add a new entry to the log in data.
    public static void appendToFile(String path, String data) throws IOException {
        File file = new File(path);
        Scanner myReader = new Scanner(file);
        String fileData = "";
            
        while (myReader.hasNextLine()) {
            fileData = fileData + myReader.nextLine() + "\n";
        }
        myReader.close();

        FileWriter fw = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.append(fileData + data);
        bw.newLine();
        bw.close();
    }

    public static ArrayList<List<String>> parseCSV (String path) {
        //Method which takes file path and parses out data in a CSV file to an array list
        try {
            File file = new File(path);
            
    
            if (file.canRead()) {
                Scanner myReader = new Scanner(file);
        
                //Initalise array list which will hold all data from csv file
                ArrayList<List<String>> newData = new ArrayList<List<String>>();
    
                while (myReader.hasNextLine()) {
                    //Will select the next line and also remove all instances of quotation marks
                    String data = myReader.nextLine();
    
                    //Split the row data based on the comma and convert that array to a list
                    String[] editedData = data.split("\"");
                    List<String> editedDataList = Arrays.asList(editedData);
        
                    //Add List to the over all Array List
                    newData.add(editedDataList);
                }
        
                myReader.close();

                //Will loop through all data gathered and remove any null entries or entries that are just commas cleaning up the data.
                ArrayList<List<String>> filteredData = new ArrayList<>();
                for (List<String> row : newData) {
                    List<String> filteredRow = new ArrayList<>();
                    for (String element : row) {
                        if (!element.equals(",") && !element.equals("")) {
                            filteredRow.add(element);
                        }
                    }
                    filteredData.add(filteredRow);
                }
    
                return filteredData;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //will take an array list input and the data to be added in an infinite arguments set up and saving that data to the list and passing it back
    public static ArrayList<List<String>> addToList (ArrayList<List<String>> data, String... results) {

        data.add(Arrays.asList(results));

        return data;
    }

    //Takes array list input and will find a specific value inside of the list and edit it's values to match the new values
    public static ArrayList<List<String>> editList (ArrayList<List<String>> data, String ID, int index, String result) {
        for (int i = 0; i < data.size(); i++) {
            if (ID.equals(data.get(i).get(0))) {
                data.get(i).set(index, result);
            }
        }

        return data;
    }

    //Takes array list input and will instead edit the entire row in the 2D ArrayList instead of just a single entry
    public static ArrayList<List<String>> editListrow (ArrayList<List<String>> data, String ID, String... results) {

        for (int i = 0; i < data.size(); i++) {
            if (ID.equals(data.get(i).get(0))) {
                List<String> newList = new ArrayList<>();

                newList.add(data.get(i).get(0));
                
                for (int e = 0; e < results.length; e++) {
                    newList.add(results[e]);
                }

                data.set(i, newList);
            }
        }

        return data;
    }

    //Takes Array list and will remove a value based off it's registration number
    public static ArrayList<List<String>> removeFromList(ArrayList<List<String>> data, String ID) {

        for (int i = 0; i < data.size(); i++) {
            if (ID.equals(data.get(i).get(0))) {
                data.remove(i);
            }
        }

        return data;
    }

    //Will take the array list and a path and will convert that 2D array back into a CSV file format and save that data
    public static void listToCSV (ArrayList<List<String>> data, String path) {
        try {
            String finalData = "";
            for (int i = 0; i < data.size(); i++) {
                String rowData = "";
                for (int n = 0; n < data.get(i).size(); n++) {
                    rowData = rowData + "\"" + data.get(i).get(n) + "\"";

                    if (n + 1 != data.get(i).size()) {
                        rowData = rowData + ",";
                    }
                }

                finalData = finalData + rowData + "\n";
            }

            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(finalData);
            bw.close();

        } catch (IOException e) {

        }
    }
}
