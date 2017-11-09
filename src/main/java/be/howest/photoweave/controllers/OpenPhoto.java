package be.howest.photoweave.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class OpenPhoto {

    public AnchorPane anchorPane;
    public Pane paneDropFile;
    public TextField textFieldImagePath;
    public JFXButton buttonEdit;
    public Pane paneLoading;

    private String imagePath;
    private Stage stage;

    private List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "bmp");

    public void initialize() {
        Platform.runLater(() -> {
            stage = (Stage) anchorPane.getScene().getWindow();
            anchorPane.requestFocus();
        });
        initializeListeners();
    }

    /* User Interface */
    private void setImagePath(File file) {
        imagePath = file.getAbsolutePath();
        updateUserInterface(imagePath);
    }

    private void updateUserInterface(String imagePath) {
        stage.requestFocus();
        buttonEdit.setDisable(false);
        textFieldImagePath.setDisable(false);
        textFieldImagePath.setPromptText("Ingeladen afbeelding");
        textFieldImagePath.setText(imagePath);
    }

    private void showLoading(boolean bool) {
        paneLoading.setVisible(bool);
        paneDropFile.setVisible(!bool);
        buttonEdit.setVisible(!bool);
        textFieldImagePath.setVisible(!bool);
    }


    /* FXML Hooks */
    public void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.bmp"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) setImagePath(file);
    }

    public void editPicture() {
        showLoading(true);
        Task<FXMLLoader> loadEditPhotoTask = getLoadEditPhotoTask();

        Thread thread = new Thread(loadEditPhotoTask);
        thread.start();
    }


    /* Event Handlers */
    private void initializeListeners() {
        paneDropFile.setOnDragOver(this::dragOverPane);
        paneDropFile.setOnDragDropped(this::dragDroppedPane);
    }

    private void dragOverPane(DragEvent event) {
        if (event.getGestureSource() != paneDropFile && event.getDragboard().hasFiles()) {
                if (!validExtensions.containsAll(
                        event.getDragboard().getFiles().stream()
                                .map(file -> getExtension(file.getName()))
                                .collect(Collectors.toList()))) {

                    event.consume();
                    return;
                }
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    // Method to to get extension of a file
    private String getExtension(String fileName){
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    private void dragDroppedPane(DragEvent event) {
        boolean success = false;
        if (event.getGestureSource() != paneDropFile && event.getDragboard().hasFiles()) {
            setImagePath(event.getDragboard().getFiles().get(0));
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }


    /* Async Task */
    private Task<FXMLLoader> getLoadEditPhotoTask() {
        Task<FXMLLoader> task = new Task<FXMLLoader>() {
            @Override
            public FXMLLoader call() throws InterruptedException {
                Thread.sleep(2000);
                return new FXMLLoader(getClass().getClassLoader().getResource("view/EditPhoto.fxml"));
            }
        };
        task.setOnSucceeded(event -> {
            Scene scene = null;
            try {
                scene = new Scene(task.getValue().load());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setMinHeight(600.0);
            stage.setMinWidth(800.0);
            stage.setTitle("PhotoWeave | Edit Photo");
            stage.getIcons().add(new Image("logo.png"));

            EditPhoto controller = task.getValue().getController();
            try {
                controller.initialize(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.initOwner(buttonEdit.getScene().getWindow());
            stage.setScene(scene);


            this.stage.close();
            stage.show();
        });
        task.setOnFailed(event -> task.getException().printStackTrace());

        return task;
    }
}