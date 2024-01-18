/* Program By Adam Cornfield */

/*
 * Contains various miscelaneous methods
 */

import java.io.IOException;

public class util {
    //Clears the commandline of any information
    public static void clearCMD() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Shift left will get rid of the left most value in array, used in command handler systems
    public static void shiftLeft(String[] array) {
        if (array.length > 1) {
            System.arraycopy(array, 1, array, 0, array.length - 1);
            array[array.length - 1] = null;  // Optional: Set the last element to null
        }
    }
}
