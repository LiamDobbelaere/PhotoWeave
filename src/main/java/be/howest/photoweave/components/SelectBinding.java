package be.howest.photoweave.components;

import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.controllers.BindingLibrary;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.util.CreateWindow;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.awt.*;
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
    private Binding SELECTED_BINDING; //temp
    private BindingPicker bindingPicker = new BindingPicker(0,0,SELECTED_BINDING);
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
        levelsChangeListener = (observable, oldValue, newValue) -> {
            SELECTED_BINDING = bindings.get(comboBoxLevels.getSelectionModel().getSelectedItem());
            bindingPicker.setBinding(SELECTED_BINDING);
        };
    }

    private void toggleLevelsChangeListener(Boolean state) {
        if (state) comboBoxLevels.getSelectionModel().selectedItemProperty().addListener(levelsChangeListener);
        else comboBoxLevels.getSelectionModel().selectedItemProperty().removeListener(levelsChangeListener);
    }

    private void initializeLevelsComboBox() {
        comboBoxLevels.setCellFactory(c -> new ColorListCell());
        comboBoxLevels.setButtonCell(new ColorListCell());
        comboBoxLevels.setTooltip(new Tooltip("Selecteer een kleur"));
    }

    private void initializeGridPane() {
        gridPane.setVgap(10);
        gridPane.add(comboBoxLevels, 0, 0);
        gridPane.add(bindingPicker, 0, 1);

        JFXButton b = new JFXButton("Verander Binding");
        //b.getStylesheets().setAll("@../style/style.css");
        b.getStyleClass().setAll("button-raised");
        b.setTooltip(new Tooltip("Opent de binding library waar je een nieuwe binding kan selecteren"));
        b.setOnMouseClicked(this::openBindingLibrary);
        gridPane.add(b, 0, 2);
    }

    private void openBindingLibrary(MouseEvent mouseEvent) {
        CreateWindow newWindow = null;
        try {
            newWindow = new CreateWindow("Binding Library", 0, 0, "view/BindingLibrary.fxml", false, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((BindingLibrary) newWindow.getController()).initialize(SELECTED_BINDING);

        newWindow.focusWaitAndShowWindow(this.getScene().getWindow(),Modality.APPLICATION_MODAL);

        SELECTED_BINDING = (((BindingLibrary) newWindow.getController()).applyBinding) ? ((BindingLibrary) newWindow.getController()).PASSED_BINDING : SELECTED_BINDING;
        bindingPicker.setBinding(SELECTED_BINDING);
        bindings.replace(comboBoxLevels.getSelectionModel().getSelectedItem(), SELECTED_BINDING);
        this.fireEvent(new BindingChanged());
    }

    public Binding getSelectedBinding() {
        return SELECTED_BINDING;
    }

    public void setBindingsMap(Map<Integer,Binding> bindings, BindingFilter bindingFilter) {
        this.bindings = bindings;
        this.bindingFilter = bindingFilter;
        if (SELECTED_BINDING == null){
            this.SELECTED_BINDING = bindings.get(bindings.keySet().iterator().next());
            if (SELECTED_BINDING != null) this.bindingPicker.setBinding(SELECTED_BINDING);
        }


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
                int colorInt = (int) Math.round(item * (255.0 / (bindingFilter.getPosterizeFilter().getLevelCount())));
                Color color = new Color(colorInt, colorInt, colorInt);

                VBox colorInfo = new VBox();
                String colorString = MessageFormat.format("rgb({0},{1},{2})", color.getRed(), color.getGreen(), color.getBlue());
                Label pane = new Label(colorString);

                int result = (colorInt < 127) ? 255 : 0;
                pane.setStyle("-fx-border-color: black;-fx-text-fill:" + String.format("#%02x%02x%02x", result, result, result) + "; -fx-border-width: 2px; -fx-background-color: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
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
        private int BINDING_SIZE = 60;
        private Label label1, label2;
        private Tooltip tooltip;

        BindingPicker(int x, int y, Binding binding) {
            this.x = x;
            this.y = y;

            label1 = new Label();
            label1.relocate(this.x * BINDING_SIZE, this.y * BINDING_SIZE);

            label2 = new Label();
            tooltip = new Tooltip();

            Tooltip.install(this, tooltip);

            getChildren().addAll(label1, label2);

            this.setTranslateX(this.x * (BINDING_SIZE * 2));
            this.setTranslateY(this.y * (BINDING_SIZE * 2));
        }

        void setBinding(Binding binding) {
            label1.setGraphic(new ImageView(ImageUtil.resample(SwingFXUtils.toFXImage(binding.getBindingImage(), null), 4)));
            label2.setText(binding.getName());
            tooltip.setText(binding.getName());
        }

    }
}
