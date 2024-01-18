/* Program By Adam Cornfield */

/*
 * Provides various functions to encrypt and decrypt via ceaser cypher for use in the log in system
 * CSR is an abbreviation of ceasar
 */

public class CSR {
    public static String Encrypt(String inputText, int shift) {
        String encryptedMessage = "";

        for (int i = 0; i < inputText.length(); i++) {
            char textVal = inputText.charAt(i);
            char shiftedVal;

            if ((textVal + shift) >= 126) {
                textVal = (char) (((textVal + shift) - 126) + 33);

                shiftedVal = (char)(textVal);
            } else {
                shiftedVal = (char)(textVal + shift);
            }

            encryptedMessage = encryptedMessage + shiftedVal;
        }

        
        return encryptedMessage;
    }

    public static String Decrypt(String inputText, int shift) {
        String Message = "";

        for (int i = 0; i < inputText.length(); i++) {
            char textVal = inputText.charAt(i);
            char shiftedVal;

            if ((textVal - shift) <= 32) {
                textVal = (char)(126 - (32 - (textVal - shift)));

                shiftedVal = (char)(textVal);
            } else {
                shiftedVal = (char)(textVal - shift);
            }

            Message = Message + shiftedVal;
        }
        
        return Message;
    }
}
