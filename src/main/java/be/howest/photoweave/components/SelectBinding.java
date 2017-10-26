package be.howest.photoweave.components;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingPalette;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by tomdo on 25/10/2017.
 */
public class SelectBinding extends VBox {
    @FXML
    private GridPane gridPane;
    private ObservableList<Binding> items = FXCollections.observableArrayList();
    private ObservableList<Integer> colorItems = FXCollections.observableArrayList();
    private JFXComboBox<Binding> comboBox = new JFXComboBox<>(items);
    private JFXComboBox<Integer> comboBoxColors = new JFXComboBox<>(colorItems);

    private BindingPalette bindingPalette;

    public SelectBinding() throws IOException {


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
        fxmlLoader.load();

        comboBox.setCellFactory(c -> new ImageListCell());
        comboBoxColors.setCellFactory(c -> new ColorListCell());

        comboBox.setButtonCell(new ImageListCell());
        comboBoxColors.setButtonCell(new ColorListCell());

        comboBox.setTooltip(new Tooltip("Select the binding for the selected color"));
        comboBoxColors.setTooltip(new Tooltip("Select color"));

        comboBoxColors.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                comboBox.getSelectionModel().select(
                        bindingPalette.getBindingPalette().get(comboBoxColors.getSelectionModel().getSelectedItem()));
            }
        });

        comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Binding>() {
            @Override
            public void changed(ObservableValue<? extends Binding> observable, Binding oldValue, Binding newValue) {
                bindingPalette.getBindingPalette().replace(
                        comboBoxColors.getSelectionModel().getSelectedItem(),
                        comboBox.getSelectionModel().getSelectedItem());
            }
        });

        gridPane.add(comboBox, 1, 0);
        gridPane.add(comboBoxColors, 0, 0);

        items.addAll(this.bindingPalette.getBindingPalette().values());
        colorItems.addAll(this.bindingPalette.getBindingPalette().keySet());

        comboBoxColors.getSelectionModel().selectFirst();

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


}
