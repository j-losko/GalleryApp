package Images.ImageClasses;

import java.io.Serializable;

public class Image implements Serializable {
    private String image;
    private String name;

    public Image(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

}
