package be.howest.photoweave.components;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingPalette;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
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

    private ChangeListener<Binding> bindingChangeListener;

    public SelectBinding() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("components/SelectBinding.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        bindingChangeListener = new ChangeListener<Binding>() {
            @Override
            public void changed(ObservableValue<? extends Binding> observable, Binding oldValue, Binding newValue) {
                bindingPalette.getBindingPalette().replace(
                        comboBoxColors.getSelectionModel().getSelectedItem(),
                        comboBox.getSelectionModel().getSelectedItem());
            }
        };

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

        comboBox.getSelectionModel().selectedItemProperty().addListener(bindingChangeListener);

        gridPane.add(comboBox, 1, 0);
        gridPane.add(comboBoxColors, 0, 0);
    }

    public void setBindingPalette(BindingPalette bindingPalette) {
        this.bindingPalette = bindingPalette;

        comboBox.getSelectionModel().selectedItemProperty().removeListener(bindingChangeListener);

        items.clear();
        colorItems.clear();

        items.addAll(this.bindingPalette.getBindingPalette().values());
        colorItems.addAll(this.bindingPalette.getBindingPalette().keySet());

        comboBoxColors.getSelectionModel().selectFirst();

        comboBox.getSelectionModel().selectedItemProperty().addListener(bindingChangeListener);
    }

    public JFXComboBox<Integer> getComboBoxColors() {
        return comboBoxColors;
    }

    public JFXComboBox<Binding> getComboBox() {
        return comboBox;
    }
}
