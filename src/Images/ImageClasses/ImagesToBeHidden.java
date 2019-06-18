package Images.ImageClasses;

import javafx.scene.control.CheckBox;

import java.io.File;

public class ImagesToBeHidden {
    private File file;
    private CheckBox checkbox;

    public ImagesToBeHidden(File file, CheckBox checkbox) {
        this.file = file;
        this.checkbox = checkbox;
    }

    public File getFile() {
        return file;
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }
}
