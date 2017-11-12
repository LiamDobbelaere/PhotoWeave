package be.howest.photoweave.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    public ListView listViewRecentFiles;

    private String imagePath;
    private Stage stage;

    private List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "bmp");

    public void initialize() {
        Platform.runLater(() -> {
            stage = (Stage) anchorPane.getScene().getWindow();
            anchorPane.requestFocus();
        });
        ObservableList<String> items = FXCollections.observableArrayList (
                "C:\\Users\\Quinten\\Pictures\\verilin\\formaat anders.png", "C:\\Users\\Quinten\\Pictures\\verilin\\POLAR.bmp", "C:\\Users\\Quinten\\Pictures\\verilin\\Results\\lionBig.bmp", "C:\\Users\\Quinten\\Pictures\\verilin\\Results\\logo.bmp");
        listViewRecentFiles.setItems(items);

        listViewRecentFiles.setCellFactory(listView -> new ListCell<String>() {
            public void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String fileName = new File(path).getName();
                    Text title = new Text(fileName);
                    Text text = new Text(path.replaceAll(fileName,""));
                    title.getStyleClass().add("vboxlistview-title");

                    VBox vBox = new VBox(title, text);
                    vBox.getStyleClass().add("vboxlistview");
                    setGraphic(vBox);

                }
            }

        });

        listViewRecentFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                System.out.println("clicked on " + listViewRecentFiles.getSelectionModel().getSelectedItem());
                setImagePath(new File(listViewRecentFiles.getSelectionModel().getSelectedItem().toString()));

            }
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
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setTitle("Verilin | PhotoWeave");
            stage.getIcons().add(new Image("logo.png"));
            stage.setScene(scene);
            stage.show();

            this.stage.close();
            
            EditPhoto controller = task.getValue().getController();
            try {
                controller.initialize(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        task.setOnFailed(event -> task.getException().printStackTrace());

        return task;
    }
}