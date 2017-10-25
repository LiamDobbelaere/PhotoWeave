package be.howest.photoweave.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.awt.Color;

/**
 * Created by tomdo on 25/10/2017.
 */
public class SelectBinding extends VBox {
    private ObservableList<BufferedImage> items = FXCollections.observableArrayList();
    private ObservableList<Color> colorItems = FXCollections.observableArrayList();
    private ComboBox<BufferedImage> comboBox = new ComboBox<>(items);
    private ComboBox<Color> comboBoxColors = new ComboBox<>(colorItems);

    public SelectBinding() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("components/SelectBinding.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        comboBox.setCellFactory(c -> new ImageListCell());
        comboBoxColors.setCellFactory(c -> new ColorListCell());

        this.getChildren().add(comboBox);
        this.getChildren().add(comboBoxColors);

        colorItems.add(Color.red);
        colorItems.add(Color.green);
        colorItems.add(Color.blue);

        try {
            for (int i = 0; i <= 24; i++) {
                items.add(ImageIO.read(new File(this.getClass().getClassLoader().getResource("bindings/shadow/" + i + ".png").toURI())));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
