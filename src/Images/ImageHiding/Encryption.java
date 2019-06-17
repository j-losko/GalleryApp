package Images.ImageHiding;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Encryption {

    //Metoda jako argument przyjmuje plik(obrazek), zamienia go na string base64
    public static String encrypt(File file) {
        String base64Image = "";
        byte[] data;
        Path path = Paths.get(file.getAbsolutePath());
        try {
            data = Files.readAllBytes(path);
            base64Image = Base64.getEncoder().encodeToString(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return base64Image;
    }

    //Metoda przyjmuje string w base64, odszyfrowuje go i zamienia spowrotem na File
    public static File decrypt(String encodedString, String location, String fileName) {
        File file = new File(fileName);
        byte[] decodedImage = Base64.getDecoder().decode(encodedString);

        Path path = Paths.get(location + file.getName());
        try {
            Files.write(path, decodedImage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return file;
    }
}
