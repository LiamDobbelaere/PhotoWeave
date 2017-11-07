package be.howest.photoweave.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;


public class OpenPhoto {

    @FXML
    private AnchorPane ap;
    @FXML
    private TextField imagepath;
    private String image;
    private Stage stage;

    public void openFilePicker() {
        stage = (Stage) ap.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("PhotoWeave: Choose Image");

        // Set extension filter
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Set initial directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            image = file.getAbsolutePath();
            imagepath.setText(image);
        }
    }

    public void editPicture() throws IOException {
        if (image == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("Please choose a picture");
            alert.showAndWait();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/EditPhoto.fxml"));

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(new Scene(loader.load()));
            stage.setMinHeight(600.0);
            stage.setMinWidth(800.0);
            stage.setTitle("PhotoWeave | Edit Photo");
            stage.getIcons().add(new Image("logo.png"));

            EditPhoto controller = loader.getController();
            controller.initData(image);

            stage.show();
            controller.zoomPhoto();
            controller.updateImage();
            this.stage.close();
        }
    }

}