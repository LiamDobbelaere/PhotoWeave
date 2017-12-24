package be.howest.photoweave.controllers;

import be.howest.photoweave.model.properties.imageProperties;
import be.howest.photoweave.model.properties.jsonProperties;
import be.howest.photoweave.model.util.ConfigUtil;
import be.howest.photoweave.model.util.CreateFilePicker;
import be.howest.photoweave.model.util.CreateWindow;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class OpenPhoto {

    public AnchorPane anchorPane;
    public Pane paneDropFile;
    public TextField textFieldImagePath;
    public JFXButton buttonEdit;
    public Pane paneLoading;
    public ListView listViewRecentFiles;
    public ImageView viewPreviewImage;
    public SVGPath fileIcon;

    private String imagePath;
    private Stage stage;

    private List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "bmp");

    public void initialize() {
        Platform.runLater(() -> {
            stage = (Stage) anchorPane.getScene().getWindow();
            anchorPane.requestFocus();
        });

        ArrayList<String> recentFiles = ConfigUtil.getRecentFiles();
        Collections.reverse(recentFiles);

        ObservableList<String> items = FXCollections.observableArrayList(recentFiles);
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
                    Text text = new Text(path.replaceAll(fileName, ""));
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
                Object selectedItem = listViewRecentFiles.getSelectionModel().getSelectedItem();
                if (selectedItem == null) return;

                File file = new File(selectedItem.toString());

                setImagePath(file);

                if (!FilenameUtils.getExtension(file.getPath()).toLowerCase().equals("json")) {
                    try {
                        setImagePreview(ImageIO.read(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        initializeListeners();
    }

    /* User Interface */
    private void setImagePath(File file) {
        imagePath = file.getAbsolutePath();
        updateUserInterface(imagePath);
    }

    private void setImagePreview(BufferedImage bi) {
        updateUserInterface(bi);
    }

    private void updateUserInterface(String imagePath) {
        stage.requestFocus();
        buttonEdit.setDisable(false);
        textFieldImagePath.setDisable(false);
        textFieldImagePath.setPromptText("Ingeladen afbeelding");
        textFieldImagePath.setText(imagePath);
    }

    private void updateUserInterface(BufferedImage bi) {
        viewPreviewImage.setImage(SwingFXUtils.toFXImage(bi, null));
        fileIcon.setVisible(false);
    }

    private void showLoading(boolean bool) {
        paneLoading.setVisible(bool);
        paneDropFile.setVisible(!bool);
        buttonEdit.setVisible(!bool);
        textFieldImagePath.setVisible(!bool);
    }


    /* FXML Hooks */
    public void openFileDialog() {
        CreateFilePicker fp = new CreateFilePicker(imageProperties.loadTitle, this.stage, imageProperties.filterDescription, imageProperties.filterExtensions);

        File file = fp.getFile();

        if (file != null) {
            setImagePath(file);
            updateImageInGUI(file);
        }
    }

    public void openNewFileDialog(ActionEvent actionEvent) {
        CreateFilePicker fp = new CreateFilePicker(imageProperties.loadTitle, this.stage, imageProperties.filterDescription, imageProperties.filterExtensions);

        File file = fp.getFile();

        if (file != null) setImagePath(file);
    }

    public void openCustomFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Verilin PhotoWeave", "*.json"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            updateRecentFiles(file);
            setImagePath(file);
        }
    }

    private void updateRecentFiles(File file) {
        ArrayList<String> recentFiles = ConfigUtil.getRecentFiles();

        if (recentFiles.contains(file.getAbsolutePath())) {
            recentFiles.remove(file.getAbsolutePath());
        }

        recentFiles.add(file.getAbsolutePath());

        ConfigUtil.getPropertiesConfig().setProperty("recentfiles", recentFiles);
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
    private String getExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    private void dragDroppedPane(DragEvent event) {
        boolean success = false;
        if (event.getGestureSource() != paneDropFile && event.getDragboard().hasFiles()) {
            setImagePath(event.getDragboard().getFiles().get(0));
            updateImageInGUI(event.getDragboard().getFiles().get(0));

            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void updateImageInGUI(File file) {
        try {
            BufferedImage bi = ImageIO.read(file);
            setImagePreview(bi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Async Task */
    private Task<FXMLLoader> getLoadEditPhotoTask() {
        Task<FXMLLoader> task = new Task<FXMLLoader>() {
            @Override
            public FXMLLoader call() throws InterruptedException {
                return new FXMLLoader(getClass().getClassLoader().getResource("view/EditPhoto.fxml"));
            }
        };
        task.setOnSucceeded(event -> {
            CreateWindow newWindow = null;
            try {
                newWindow = new CreateWindow("Verilin | PhotoWeave", 800.0, 600.0, task.getValue(), true, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            CreateWindow finalNewWindow = newWindow;

            newWindow.showWindow();

            this.stage.close();

            try {
                ((EditPhoto) newWindow.getController()).initialize(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ScrollPane sp = ((EditPhoto) newWindow.getController()).getImageScrollPane();
            sp.layout();

            //sp.setVvalue(((EditPhoto)  newWindow.getController()).getYScroll());
            //sp.setHvalue(((EditPhoto)  newWindow.getController()).getXScroll());


        });
        task.setOnFailed(event -> task.getException().printStackTrace());

        return task;
    }
}