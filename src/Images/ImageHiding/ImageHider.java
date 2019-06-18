package Images.ImageHiding;

import Images.ImageClasses.Image;
import Images.ImageClasses.Images;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class ImageHider {

    //Metoda przyjmuje liste obrazków a następnie wrzuca je do pliku podanego jako argument location
    public static void hideImages(ArrayList<File> files, String location, String password) {
        ArrayList<Image> imageList = new ArrayList<>();

        for (File file : files) {
            imageList.add(new Image(Encryption.encrypt(file), file.getName()));
        }
        Images images = new Images(imageList, password);

        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(location);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(images);

            out.close();
            file.close();

            // the images were saved in the hidden file - only now can we delete them:
            for (File f : files) {
                Files.delete(f.toPath());
            }

            System.out.println("Object has been serialized");
        } catch (IOException ex) {
            System.out.println("IOException is caught");
            ex.printStackTrace();
        }
    }

    //Metoda przyjmuje plik w którym ukryte są obrazki a następnie rozpakowuje je do podanego folderu
    public static void showImages(File file, String location, String password) throws WrongPasswordException {
        Images images = null;
        try {
            // Reading the object from a file
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            // Method for deserialization of object
            images = (Images) in.readObject();

            in.close();
            fileIn.close();

            if (!images.getPassword().equals(password))
                throw new WrongPasswordException();

            for (Image image : images.getImages()) {
                Encryption.decrypt(image.getImage(), location, image.getName());
            }

            Files.delete(file.toPath());

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }
    }
}
