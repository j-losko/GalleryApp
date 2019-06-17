package Images.ImageHiding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

public class Encryption {

    //Metoda jako argument przyjmuje plik(obrazek), zamienia go na string base64
    public static String encrypt(File file){
        String base64Image = "";
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading a Image file from file system
            byte[] imageData = new byte[(int) file.length()];
            imageInFile.read(imageData);
            base64Image = Base64.getEncoder().encodeToString(imageData);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }

        return base64Image;
    }

    //Metoda przyjmuje string w base64, odszyfrowuje go(i zamienia spowrotem na File ?? not sure)
    public static String decrypt(String encodedString){

        return "";
    }
}
