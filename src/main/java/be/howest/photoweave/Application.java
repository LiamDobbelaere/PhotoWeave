package be.howest.photoweave;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {


    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("view/OpenPhoto.fxml"));

        Scene scene = new Scene(pane);
        stage.setTitle("Choose Image");
        stage.setScene(scene);
        stage.show();
    }
}
