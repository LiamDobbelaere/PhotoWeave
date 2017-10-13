package be.howest.photoweave.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("All Images", "*.*")
        );

        // Set initial directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            image = file.getAbsolutePath();
            imagepath.setText(image);
        }
    }

    public Stage editPicture() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/EditPhoto.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene((Pane) loader.load()));
        stage.setMinHeight(600.0);
        stage.setMinWidth(800.0);
        stage.setTitle("PhotoWeave: Edit Photo");

        EditPhoto controller = loader.<EditPhoto>getController();
        controller.initData(image);

        stage.show();

        return stage;
    }

}
