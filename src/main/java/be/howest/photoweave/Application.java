package be.howest.photoweave;

import be.howest.photoweave.components.CreateWindow;
import be.howest.photoweave.controllers.OpenPhoto;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        
        CreateWindow newWindow = new CreateWindow("Verilin | PhotoWeave", 800.0, 600.0, "view/OpenPhoto.fxml", false, true);
        ((OpenPhoto)newWindow.getController()).initialize();
        newWindow.showWindow();

    }
}
