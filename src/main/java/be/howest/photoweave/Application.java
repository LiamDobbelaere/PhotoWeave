package be.howest.photoweave;

import be.howest.photoweave.model.util.ConfigUtil;
import be.howest.photoweave.model.util.CreateDirectoryPicker;
import be.howest.photoweave.model.util.CreateWindow;
import be.howest.photoweave.controllers.OpenPhoto;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.configuration2.PropertiesConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        boolean forceQuit = false;
        while (!forceQuit && !Files.exists(Paths.get(ConfigUtil.getBindingsPath()))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Fout!");
            alert.setContentText("Het pad voor de bindings:\n" + ConfigUtil.getBindingsPath() + "\nkan niet worden gevonden!");

            ButtonType closePhotoWeave = new ButtonType("PhotoWeave Sluiten");
            ButtonType chooseDifferentPath = new ButtonType("Ander pad kiezen");

            alert.getButtonTypes().setAll(closePhotoWeave, chooseDifferentPath);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == closePhotoWeave) {
                forceQuit = true;
                Platform.exit();
            } else if (result.get() == chooseDifferentPath) {
                File newPath = new CreateDirectoryPicker("Kies map naar bindings").show();

                if (newPath != null) {
                    ConfigUtil.setBindingsPath(newPath.getAbsolutePath());
                }
            }
        }

        
        CreateWindow newWindow = new CreateWindow("Verilin | PhotoWeave", 0, 0, "view/OpenPhoto.fxml", false, true);
        ((OpenPhoto)newWindow.getController()).initialize();
        newWindow.showWindow();

    }
}
