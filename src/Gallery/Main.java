package Gallery;

import Images.ImageHiding.WrongPasswordException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Main extends Application {

    private Stage stage;
    private Image folderImage;
    private Image fileImage;
    private Image brokenImage;
    private Image lockImage;
    private Image unlockImage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Gallery");
        stage.setResizable(false);

        folderImage = new Image(Main.class.getResourceAsStream("imgs/folder.png"));
        fileImage = new Image(Main.class.getResourceAsStream("imgs/file.png"));
        brokenImage = new Image(Main.class.getResourceAsStream("imgs/broken.png"));
        lockImage = new Image(Main.class.getResourceAsStream("imgs/lock.png"));
        unlockImage = new Image(Main.class.getResourceAsStream("imgs/unlock.png"));

        Path currentPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        File currentFolder = new File(currentPath.toString());

        showFolder(currentFolder);
    }

    private void showFolder(File currentFolder) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.WHITE);
        stage.setScene(scene);

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(30, 10, 10, 10));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        File[] allSubFiles = currentFolder.listFiles();

        int column = 0;
        int row = 0;

        if (currentFolder.getParent() != null) {
            Button buttonUp = new Button();
            buttonUp.setGraphic(new ImageView(folderImage));
            buttonUp.setOnAction(value -> {
                System.out.println("Folder do góry: " + currentFolder.getParent());
                showFolder(currentFolder.getParentFile());
            });
            gridpane.add(new VBox(buttonUp, new Text("../")), column++, row);
        }

        if (allSubFiles == null) {
            return;
        }
        for (File file : allSubFiles) {
            Text text = new Text(file.getName());
            text.setWrappingWidth(64);
            Button button = new Button();

            if (file.isDirectory()) {
                button.setGraphic(new ImageView(folderImage));
                button.setOnAction(value -> {
                    System.out.println("Navigate to: " + file.getName());
                    showFolder(file);
                });
            } else {
                String extension = "";
                int i = file.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = file.getName().substring(i + 1);
                }
                switch (extension) {
                    case "bmp":
                    case "gif":
                    case "jpg":
                    case "jpeg":
                    case "png":
                        try {
                            Image thumbnail = new Image(file.toURI().toString(), 48, 48, false, true);
                            button.setGraphic(new ImageView(thumbnail));
                            button.setOnAction(value -> {
                                System.out.println("Open image: " + file.getName());
                                showImage(file, scene);
                            });
                        } catch (Exception e) {
                            button.setGraphic(new ImageView(brokenImage));
                        }
                        break;
                    case "hidden":
                        button.setGraphic(new ImageView(unlockImage));
                        button.setOnAction(value -> {
                            System.out.println("Unlock image: " + file.getName());
                            unhideImages(file, "asd"); //TODO żeby okienko z passwordem się pojawiało
                        });
                        break;
                    default:
                        button.setGraphic(new ImageView(fileImage));
                        button.setOnAction(value -> System.out.println("Not an image!: " + file.getName()));
                        break;
                }
            }

            gridpane.add(new VBox(button, text), column++, row);

            if (column >= 10) {
                column = 0;
                ++row;
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridpane);

        scrollPane.setPrefSize(800, 600);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.getChildren().add(scrollPane);

        root.getChildren().add(new Text(10, 15, currentFolder.getAbsolutePath()));
        stage.show();
    }

    private void showImage(File imageFile, Scene oldScene) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.WHITE);
        stage.setScene(scene);

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5, 10, 10, 10));

        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView(image);

        Text filePathText = new Text(10, 0, imageFile.getAbsolutePath());

        Button buttonUp = new Button();
        buttonUp.setGraphic(new ImageView(folderImage));
        buttonUp.setOnAction(value -> {
            System.out.println("Wróć do widoku galerii");
            stage.setScene(oldScene);
        });
        VBox goToGalleryVBox = new VBox(buttonUp, new Text("../"));

        Button hideImage = new Button();
        hideImage.setGraphic(new ImageView(lockImage));
        hideImage.setOnAction(value -> {
            System.out.println("Navigate to hide single image");
            hideSingleImage(imageFile, oldScene);
        });
        VBox hideImageVBox = new VBox(hideImage, new Text("Hide image"));

        HBox hbox = new HBox(goToGalleryVBox, hideImageVBox);
        hbox.setSpacing(20);

        VBox vbox = new VBox(filePathText, hbox, imageView);
        vbox.setSpacing(10);
        gridpane.add(vbox, 0, 0);

        ScrollPane scrollPane = new ScrollPane(gridpane);

        scrollPane.setPannable(true);
        scrollPane.setPrefSize(800, 600);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        root.getChildren().add(scrollPane);

        stage.show();
    }

    private void hideSingleImage(File imageFile, Scene oldScene) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.WHITE);
        stage.setScene(scene);
        stage.setTitle("Hide image");

        VBox dialogVBox = new VBox(20);

        final PasswordField passwordField = new PasswordField();
        final TextField hiddenFileName = new TextField();

        Button hideImage = new Button();
        hideImage.setGraphic(new ImageView(lockImage));
        hideImage.setOnAction(value -> {
            System.out.println("Ukryj obrazek");
            ArrayList<File> imageToHide = new ArrayList<>();
            imageToHide.add(imageFile);
            System.out.println(imageFile.getParent() + passwordField.getText());
            Images.ImageHiding.ImageHider.hideImages(imageToHide, (imageFile.getParent() + "\\" + hiddenFileName.getText() + ".hidden"), passwordField.getText());
            stage.setScene(oldScene);
        });

        Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(value -> stage.setScene(oldScene));

        dialogVBox.getChildren().addAll(new Text("Password"), passwordField, new Text("Hidden file name"), hiddenFileName, hideImage, cancel);

        root.getChildren().add(dialogVBox);
        stage.show();
    }

    private void unhideImages(File file, String password) {
        //TODO modal maybe?

        try {
            Images.ImageHiding.ImageHider.showImages(file, file.getParent() + "\\", password);
        } catch(WrongPasswordException ex) {
            //TODO zły password
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

