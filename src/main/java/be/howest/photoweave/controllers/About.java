package be.howest.photoweave.controllers;

import be.howest.photoweave.model.util.CreateWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class About {
    public AnchorPane anchorPane;

    public void showApache20License(ActionEvent actionEvent) throws IOException {
        CreateWindow newWindow = new CreateWindow("Licence",0,0,"view/License.fxml", false, true);
        Scene scene = newWindow.getScene();

        TextArea textArea = (TextArea) scene.lookup("#license");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("licenses/apache20").getFile());

        StringBuilder result = new StringBuilder("");
        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        textArea.setText(result.toString());

        newWindow.focusWaitAndShowWindow(this.anchorPane.getScene().getWindow(), Modality.APPLICATION_MODAL);
    }
}
