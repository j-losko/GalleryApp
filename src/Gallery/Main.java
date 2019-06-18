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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Main extends Application {

    private Stage stage;
    private final Image folderImage = new Image(Main.class.getResourceAsStream("imgs/folder.png"));
    private final Image fileImage = new Image(Main.class.getResourceAsStream("imgs/file.png"));
    private final Image brokenImage = new Image(Main.class.getResourceAsStream("imgs/broken.png"));
    private final Image lockImage = new Image(Main.class.getResourceAsStream("imgs/lock.png"));
    private final Image unlockImage = new Image(Main.class.getResourceAsStream("imgs/unlock.png"));
    private File currentFolder;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Gallery");
        stage.setResizable(false);

        Path currentPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        currentFolder = new File(currentPath.toString());

        showFolder(currentFolder);
    }

    private void showFolder(File currentFolder) {
        final Group root = new Group();
        final Scene scene = new Scene(root, 800, 600, Color.WHITE);
        stage.setScene(scene);

        final GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(30, 10, 10, 10));
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        File[] allSubFiles = currentFolder.listFiles();

        int column = 0;
        int row = 0;

        if (currentFolder.getParent() != null) {
            final Button buttonUp = new Button();
            buttonUp.setGraphic(new ImageView(folderImage));
            buttonUp.setOnAction(value -> {
                System.out.println("Folder do góry: " + currentFolder.getParent());
                this.currentFolder = currentFolder.getParentFile();
                showFolder(this.currentFolder);
            });
            gridpane.add(new VBox(buttonUp, new Text("../")), column++, row);
        }

        if (allSubFiles == null) {
            return;
        }
        for (File file : allSubFiles) {
            final Text text = new Text(file.getName());
            text.setWrappingWidth(64);
            final Button button = new Button();

            if (file.isDirectory()) {
                button.setGraphic(new ImageView(folderImage));
                button.setOnAction(value -> {
                    System.out.println("Navigate to: " + file.getName());
                    this.currentFolder = file;
                    showFolder(this.currentFolder);
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
                            final Image thumbnail = new Image(file.toURI().toString(), 48, 48, false, true);
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
                            unhideImages(file);
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

        final ScrollPane scrollPane = new ScrollPane(gridpane);

        scrollPane.setPrefSize(800, 600);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.getChildren().add(scrollPane);

        root.getChildren().add(new Text(10, 15, currentFolder.getAbsolutePath()));
        stage.show();
    }

    private void showImage(File imageFile, Scene oldScene) {
        final Group root = new Group();
        final Scene scene = new Scene(root, 800, 600, Color.WHITE);
        stage.setScene(scene);

        final GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5, 10, 10, 10));

        final Image image = new Image(imageFile.toURI().toString());
        final ImageView imageView = new ImageView(image);

        final Text filePathText = new Text(10, 0, imageFile.getAbsolutePath());

        final Button buttonUp = new Button();
        buttonUp.setGraphic(new ImageView(folderImage));
        buttonUp.setOnAction(value -> {
            System.out.println("Wróć do widoku galerii");
            stage.setScene(oldScene);
        });
        final VBox goToGalleryVBox = new VBox(buttonUp, new Text("../"));

        final Button hideImage = new Button();
        hideImage.setGraphic(new ImageView(lockImage));
        hideImage.setOnAction(value -> {
            System.out.println("Navigate to hide single image");
            hideSingleImage(imageFile);
        });
        final VBox hideImageVBox = new VBox(hideImage, new Text("Hide image"));

        final HBox hbox = new HBox(goToGalleryVBox, hideImageVBox);
        hbox.setSpacing(20);

        final VBox vbox = new VBox(filePathText, hbox, imageView);
        vbox.setSpacing(10);
        gridpane.add(vbox, 0, 0);

        final ScrollPane scrollPane = new ScrollPane(gridpane);

        scrollPane.setPannable(true);
        scrollPane.setPrefSize(800, 600);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        root.getChildren().add(scrollPane);

        stage.show();
    }

    private void hideSingleImage(File imageFile) {
        final Group root = new Group();
        final Scene scene = new Scene(root, 800, 600, Color.WHITE);
        stage.setScene(scene);
        stage.setTitle("Hide image");

        final VBox vbox = new VBox(20);

        final PasswordField passwordField = new PasswordField();
        final TextField hiddenFileName = new TextField();

        final Button hideImage = new Button();
        hideImage.setGraphic(new ImageView(lockImage));
        hideImage.setOnAction(value -> {
            System.out.println("Ukryj obrazek");
            final ArrayList<File> imageToHide = new ArrayList<>();
            imageToHide.add(imageFile);
            System.out.println(imageFile.getParent() + passwordField.getText());
            Images.ImageHiding.ImageHider.hideImages(imageToHide, (imageFile.getParent() + "\\" + hiddenFileName.getText() + ".hidden"), passwordField.getText());
            showFolder(currentFolder);
        });

        final Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(value -> showFolder(currentFolder));

        vbox.getChildren().addAll(new Text("Password"), passwordField, new Text("Hidden file name"), hiddenFileName, hideImage, cancel);

        root.getChildren().add(vbox);
        stage.show();
    }

    private void unhideImages(File file) {
        //TODO modal maybe?
        final Stage unlockStage = new Stage();
        unlockStage.initModality(Modality.APPLICATION_MODAL);
        unlockStage.initOwner(stage);
        final VBox vbox = new VBox(20);
        vbox.getChildren().add(new Text("Wpisz hasło:"));

        final PasswordField passwordField = new PasswordField();
        vbox.getChildren().add(passwordField);

        final Button button = new Button();
        button.setText("OK");
        button.setOnAction(value -> {
            try {
                Images.ImageHiding.ImageHider.showImages(file, file.getParent() + "\\", passwordField.getText());
                showFolder(currentFolder);
                unlockStage.close();
            } catch(WrongPasswordException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong password!");
                alert.setHeaderText(null);
                alert.setContentText("Wrong password!");
                alert.showAndWait();
            }
        });
        vbox.getChildren().add(button);

        final Scene unlockScene = new Scene(vbox, 300, 200);
        unlockStage.setScene(unlockScene);
        unlockStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

