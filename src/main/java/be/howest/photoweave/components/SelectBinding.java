package be.howest.photoweave.components;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingPalette;
import be.howest.photoweave.model.util.ImageUtil;
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
    private ObservableList<Binding> items = FXCollections.observableArrayList();
    private ObservableList<Integer> colorItems = FXCollections.observableArrayList();
    private ComboBox<Binding> comboBox = new ComboBox<>(items);
    private ComboBox<Integer> comboBoxColors = new ComboBox<>(colorItems);

    private BindingPalette bindingPalette;

    public SelectBinding() {
        //Todo: delete this, bindingPalette should be set to the WovenImage's BindingPalette
        try {
            this.bindingPalette = new BindingPalette(
                    ImageUtil.convertImageToRGBInt(
                            ImageIO.read(new File(this.getClass().getClassLoader().getResource("test/polar_24levels.png").toURI()))));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("components/SelectBinding.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        comboBox.setCellFactory(c -> new ImageListCell());
        comboBoxColors.setCellFactory(c -> new ColorListCell());

        this.getChildren().add(comboBox);
        this.getChildren().add(comboBoxColors);

        items.addAll(this.bindingPalette.getBindingPalette().values());
        colorItems.addAll(this.bindingPalette.getBindingPalette().keySet());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
