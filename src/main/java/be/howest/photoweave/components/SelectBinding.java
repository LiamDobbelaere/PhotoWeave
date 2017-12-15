package be.howest.photoweave.components;

import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

/**
 * Created by tomdo on 25/10/2017.
 */
public class SelectBinding extends VBox {
    @FXML
    private GridPane gridPane;
    private ObservableList<Binding> bindingsList = FXCollections.observableArrayList();
    private ObservableList<Integer> levelsList = FXCollections.observableArrayList();
    private JFXComboBox<Binding> comboBoxBindings = new JFXComboBox<>(bindingsList);
    private JFXComboBox<Integer> comboBoxLevels = new JFXComboBox<>(levelsList);

    private Map<Integer, Binding> bindings;
    private ChangeListener<Binding> bindingChangeListener;

    public SelectBinding() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("components/SelectBinding.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        bindingChangeListener = (observable, oldValue, newValue) -> {
            System.out.println("Binding change listener!");
                bindings.replace(
                    comboBoxLevels.getSelectionModel().getSelectedItem(),
                    comboBoxBindings.getSelectionModel().getSelectedItem());
            comboBoxBindings.fireEvent(new BindingChanged());
        };

        comboBoxBindings.setCellFactory(c -> new ImageListCell());
        comboBoxLevels.setCellFactory(c -> new ColorListCell());

        comboBoxBindings.setButtonCell(new ImageListCell());
        comboBoxLevels.setButtonCell(new ColorListCell());

        comboBoxBindings.setTooltip(new Tooltip("Select the binding for the selected color"));
        comboBoxLevels.setTooltip(new Tooltip("Select color"));

        comboBoxLevels.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                //comboBoxBindings.getSelectionModel().selectedItemProperty().removeListener(bindingChangeListener);
                comboBoxBindings.getSelectionModel().select(
                        bindings.get(comboBoxLevels.getSelectionModel().getSelectedItem()));
                //comboBoxBindings.getSelectionModel().selectedItemProperty().addListener(bindingChangeListener);
            }
        });

        comboBoxBindings.getSelectionModel().selectedItemProperty().addListener(bindingChangeListener);

        gridPane.add(comboBoxBindings, 1, 0);
        gridPane.add(comboBoxLevels, 0, 0);
    }

    public void setBindingsMap(Map<Integer, Binding> bindings, BindingFactory bindingFactory) {
        this.bindings = bindings;

        Integer selectedItem = comboBoxLevels.getSelectionModel().getSelectedItem();

        comboBoxBindings.getSelectionModel().selectedItemProperty().removeListener(bindingChangeListener);

        bindingsList.clear();
        levelsList.clear();

        bindingsList.addAll(bindingFactory.getOptimizedBindings());
        levelsList.addAll(bindings.keySet());

        if (levelsList.contains(selectedItem)) {
            System.out.println("nope");
            comboBoxLevels.getSelectionModel().select(selectedItem);
        } else {
            comboBoxLevels.getSelectionModel().selectFirst();
        }

        comboBoxBindings.getSelectionModel().selectedItemProperty().addListener(bindingChangeListener);
    }

    public JFXComboBox<Integer> getComboBoxLevels() {
        return comboBoxLevels;
    }

    public JFXComboBox<Binding> getComboBoxBindings() {
        return comboBoxBindings;
    }


    class ImageListCell extends JFXListCell<Binding> {
        private ImageView iv = new ImageView();



        @Override
        protected void updateItem(Binding item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);

            if (item != null) {
                iv.setImage(ImageUtil.resample(SwingFXUtils.toFXImage(item.getBindingImage(), null), 4));

                setGraphic(iv);

                //setText("Binding name goes here");

            }
        }
    }

    class ColorListCell extends JFXListCell<Integer> {
        //private BorderPane pane = new BorderPane();
        private Label pane = new Label("?");

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            setGraphic(null);
            setText(null);

            if (item != null) {
                int colorInt = 128;
                //int colorInt = (int) Math.round(item * (255.0 / (bindingFilter.getPosterizeFilter().getLevelCount() - 1)));
                Color color = new Color(colorInt, colorInt, colorInt);


                pane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                pane.setPrefSize(40, 40);
                pane.setMinSize(40, 40);
                pane.setMaxSize(40, 40);

                setGraphic(pane);

            }
        }
    }

}
