package be.howest.photoweave.components;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColorBindingLinker {
    //GUI elements
    public ScrollPane scrollpane;
    public PixelatedImageView imageview;
    public VBox vbox;

    private FilteredImage filteredImage;
    private BindingFilter bindingFilter;

    private Map<Integer, Binding> bindingMap;

    private List<Integer> sortedColors;
    private List<Binding> sortedBindings;
    private Binding[] allBindings;

    private ObservableList<Binding> items = FXCollections.observableArrayList();
    private ObservableList<Integer> colorItems = FXCollections.observableArrayList();
    private ChangeListener<Binding> bindingChangeListener;

    public void initialize(FilteredImage filteredImage) {
        this.filteredImage = filteredImage;

        this.bindingFilter = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);

        this.bindingMap = bindingFilter.getBindingsMap();

        this.sortedBindings = new ArrayList<>(bindingMap.values());
        this.sortedColors = new ArrayList<>(bindingMap.keySet());
        this.allBindings = bindingFilter.getBindingFactory().getOptimizedBindings();

        this.vbox = new VBox();

        imageview.setImage(SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null));

        bindingChangeListener = new ChangeListener<Binding>() {
            @Override
            public void changed(ObservableValue<? extends Binding> observable, Binding oldValue, Binding newValue) {

                //comboBox.fireEvent(new BindingChanged());
            }
        };

        items.clear();
        items.addAll(this.allBindings);

        colorItems.clear();
        colorItems.addAll(this.sortedColors);

        for (int i = 0; i < colorItems.size(); i++) {
            HBox hbox = new HBox();

            hbox.getChildren().add(makeColorPalet(colorItems.get(i)));
            hbox.getChildren().add(makeBindingComboBox(i));

            vbox.getChildren().add(hbox);
        }
        scrollpane.setContent(vbox);
    }

    public BorderPane makeColorPalet(Integer colorCode) {

        BorderPane pane = new BorderPane();

        int colorInt = (int) Math.round(colorCode * (255.0 / (bindingFilter.getPosterizeFilter().getLevelCount() - 1)));

        Color color = new Color(colorInt, colorInt, colorInt);

        pane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
        pane.setPrefSize(40, 40);
        pane.setMinSize(40, 40);
        pane.setMaxSize(40, 40);

        return pane;
    }

    public JFXComboBox<Binding> makeBindingComboBox(int i) {
        JFXComboBox<Binding> comboBox = new JFXComboBox<>(items);

        comboBox.setCellFactory(c -> new ImageListCell());
        comboBox.setButtonCell(new ImageListCell());
        comboBox.setTooltip(new Tooltip("Select the binding for the selected color"));
        comboBox.getSelectionModel().selectedItemProperty().addListener(bindingChangeListener);
        comboBox.getSelectionModel().select(i);

        return comboBox;
    }

    public void saveBindingLibrary(ActionEvent actionEvent) {

        List<Binding> newBindingsMap = new ArrayList<>();

        for (int i = 0; i < vbox.getChildren().size(); i++) {
            Binding binding = null;
            String colorCode;
            Color color;
            int colorInt = 0;
            Node nodeOut = vbox.getChildren().get(i);
            if (nodeOut instanceof HBox) {
                for (Node nodeIn : ((HBox) nodeOut).getChildren()) {
                    if (nodeIn instanceof BorderPane) {
                        colorCode = ((BorderPane) nodeIn).getBackground().getFills().get(0).getFill().toString();
                        color = new Color((colorCode.charAt(2) + colorCode.charAt(3)), (colorCode.charAt(4) + colorCode.charAt(5)), (colorCode.charAt(6) + colorCode.charAt(7)));
                        colorInt = color.getRGB();

                    }
                    if (nodeIn instanceof JFXComboBox) {
                        binding = ((Binding) ((JFXComboBox) nodeIn).getSelectionModel().getSelectedItem());
                        newBindingsMap.add(binding);
                    }
                }
            }
        }

        int i = 0;
        for (Map.Entry<Integer, Binding> entry : bindingMap.entrySet()) {
            bindingMap.put(entry.getKey(), newBindingsMap.get(i));
        }

        bindingFilter.setBindingsMap(bindingMap);
        filteredImage.redraw();
        Stage stage = (Stage) scrollpane.getScene().getWindow();
        stage.close();
    }

    class ImageListCell extends JFXListCell<Binding> {
        private javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView();

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
}