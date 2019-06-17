package Images.ImageClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class Images implements Serializable {
    private ArrayList<Image> images;
    private String password;

    public Images(ArrayList<Image> images, String password) {
        this.images = images;
        this.password = password;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public String getPassword() {
        return password;
    }
}
