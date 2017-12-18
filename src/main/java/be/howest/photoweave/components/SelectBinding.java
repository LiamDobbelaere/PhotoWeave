package be.howest.photoweave.components;

//TODO Rechts klikken op ComboBoxBindings open je de BindingLibrary; Links is default combobox.

import be.howest.photoweave.controllers.CalculateFlattening;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by tomdo on 25/10/2017.
 */
public class SelectBinding extends VBox {
    @FXML
    private GridPane gridPane;
    private ObservableList<Binding> bindingsList = FXCollections.observableArrayList();
    private ObservableList<Integer> levelsList = FXCollections.observableArrayList();
    private JFXComboBox<Integer> comboBoxLevels = new JFXComboBox<>(levelsList);

    private Map<Integer, Binding> bindings;

    //new code
    private BindingPicker bindingPicker = new BindingPicker(0,0,false,false,new BindingFactory().getOptimizedBindings()[0]);
    private BindingFilter bindingFilter;
    private ChangeListener levelsChangeListener;


    public SelectBinding() throws IOException {
        loadMe();
        initializeLevelsListener();
        toggleLevelsChangeListener(true);
        initializeLevelsComboBox();
        initializeGridPane();
    }


    private void loadMe() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("components/SelectBinding.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    private void initializeLevelsListener() {
        levelsChangeListener = (observable, oldValue, newValue) -> { bindingPicker.setBinding(bindingFilter.getBindingsMap().get(comboBoxLevels.getSelectionModel().getSelectedItem()).getBindingImage()); };
    }

    private void toggleLevelsChangeListener(Boolean state){
        if (state) comboBoxLevels.getSelectionModel().selectedItemProperty().addListener(levelsChangeListener);
        else comboBoxLevels.getSelectionModel().selectedItemProperty().removeListener(levelsChangeListener);
    }

    private void initializeLevelsComboBox(){
        comboBoxLevels.setCellFactory(c -> new ColorListCell());
        comboBoxLevels.setButtonCell(new ColorListCell());
        comboBoxLevels.setTooltip(new Tooltip("Selecteer een kleur"));
    }

    private void initializeGridPane(){
        gridPane.setVgap(10);
        gridPane.add(comboBoxLevels, 0, 0);
        gridPane.add(bindingPicker,0,1);
        Button b = new Button("Verander binding");
        b.setOnMouseClicked(this::testOpenib);
        gridPane.add(b,1,1);
    }

    private void testOpenib(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/BindingLibrary.fxml"));

        Scene scene = null;

        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }


        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("Binding Library");
        stage.setScene(scene);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }


    public void setBindingsMap(Map<Integer, Binding> bindings) {
        this.bindings = bindings;

        Integer selectedItem = comboBoxLevels.getSelectionModel().getSelectedItem();

        toggleLevelsChangeListener(false);

        bindingsList.clear();
        levelsList.clear();

        bindingsList.addAll(bindings.values());
        levelsList.addAll(bindings.keySet());

        if (levelsList.contains(selectedItem))
            comboBoxLevels.getSelectionModel().select(selectedItem);
        else
            comboBoxLevels.getSelectionModel().selectFirst();

        toggleLevelsChangeListener(true);
    }

    public JFXComboBox<Integer> getComboBoxLevels() {
        return comboBoxLevels;
    }

    private class ColorListCell extends JFXListCell<Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            setGraphic(null);
            setText(null);

            if (item != null) {
                //int colorInt = (int) Math.round(item * (255.0 / (bindingFilter.getPosterizeFilter().getLevelCount() - 1)));
                Color color = new Color(123, 123, 123);

                VBox colorInfo = new VBox();
                String colorString = MessageFormat.format("rgb({0} {1} {2})", color.getRed(), color.getGreen(), color.getBlue());
                Label pane = new Label(colorString);

                pane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                pane.setPrefSize(140, 40);
                pane.setMinSize(140, 40);
                pane.setMaxSize(140, 40);

                colorInfo.getChildren().add(pane);
                setGraphic(colorInfo);
            }
        }
    }

    private class BindingPicker extends VBox {
        private int x, y;
        private Binding binding;
        private int BINDING_SIZE = 60;
        private Label label1, label2;

        BindingPicker(int x, int y, boolean isFilled, boolean hasStroke, Binding binding) {
            this.x = x;
            this.y = y;
            this.binding = binding;

            label1 = new Label();
            label1.relocate(this.x * BINDING_SIZE, this.y * BINDING_SIZE);
            label1.setGraphic(new ImageView(ImageUtil.resample(SwingFXUtils.toFXImage(this.binding.getBindingImage(),null),4)));

            label2 = new Label("Binding4");
            Tooltip tooltip = new Tooltip("Binding4");
            Tooltip.install(this, tooltip);

            getChildren().addAll(label1,label2);

            this.setTranslateX(this.x * (BINDING_SIZE * 2));
            this.setTranslateY(this.y * (BINDING_SIZE * 2));
        }

        void setBinding(BufferedImage binding){
            label1.setGraphic(new ImageView(ImageUtil.resample(SwingFXUtils.toFXImage(binding,null),4)));
        }

    }
}
