package be.howest.photoweave.model.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class CreateWindow {

    private String title;
    private double width;
    private double height;
    private String logoPath;
    private boolean resizable;
    private boolean sizeToScene;

    private Object controller;

    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;

    private CreateWindow(String title, double width, double height, boolean resizable, boolean sizeToScene) throws IOException {
        this.title = title;
        this.width = width;
        this.height = height;
        this.sizeToScene = sizeToScene;
        this.resizable = resizable;
        this.logoPath = "logo.png";
    }

    public CreateWindow(String title, double width, double height, FXMLLoader loader, boolean resizable, boolean sizeToScene) throws IOException {
        this(title, width, height, resizable, sizeToScene);
        this.setFXMLLoader(loader);
        this.createWindow();
    }

    public CreateWindow(String title, double width, double height, String fxmlFilePath, boolean resizable, boolean sizeToScene) throws IOException {
        this(title, width, height, resizable, sizeToScene);
        this.loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlFilePath));
        this.setFXMLLoader(loader);
        this.createWindow();
    }

    private void createWindow() throws IOException {

        scene = new Scene(loader.load());

        controller = loader.getController();

        stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.setResizable(resizable);
        stage.setTitle(title);
        stage.getIcons().add(new Image(logoPath));

        if (sizeToScene) {
            stage.sizeToScene();
        } else {
            stage.setMinHeight(height);
            stage.setMinWidth(width);
        }
    }

    public void focusWaitAndShowWindow(Window owner, Modality modality) {
        stage.initOwner(owner);
        stage.initModality(modality);
        stage.showAndWait();
    }

    public void showWindow() {
        stage.show();
    }

    public Object getController() {
        return controller;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }
    
    private void setFXMLLoader(FXMLLoader loader) {
        this.loader = loader;
    }

}
