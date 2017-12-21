package be.howest.photoweave.components;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
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
    public AnchorPane anchorpane;
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

        imageview.setImage(SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null));

        this.bindingFilter = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);

        this.bindingMap = bindingFilter.getBindingsMap();

        this.sortedBindings = new ArrayList<>(bindingMap.values());
        this.sortedColors = new ArrayList<>(bindingMap.keySet());
        this.allBindings = bindingFilter.getBindingFactory().getOptimizedBindings();

        this.bindingChangeListener = (observable, oldValue, newValue) -> {
            System.out.println("Binding change listener!");

            int id = getColorFromBinding(newValue);

            bindingMap.replace(id, newValue);
        };

        this.vbox = new VBox();

        items.clear();
        items.addAll(this.allBindings);

        colorItems.clear();
        colorItems.addAll(this.sortedColors);

        for (int i = 0; i < colorItems.size(); i++) {
            HBox hbox = new HBox();

            hbox.getChildren().add(makeColorPalet(i, colorItems.get(i)));
            hbox.getChildren().add(makeBindingComboBox(i, sortedBindings));

            vbox.getChildren().add(hbox);
        }

        scrollpane.setContent(vbox);
    }

    public BorderPane makeColorPalet(int i, Integer colorCode) {

        BorderPane pane = new BorderPane();

        int colorInt = (int) Math.round(colorCode * (255.0 / (bindingFilter.getPosterizeFilter().getLevelCount() - 1)));

        Color color = new Color(colorInt, colorInt, colorInt);

        pane.setId(String.valueOf(colorCode));
        pane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
        pane.setPrefSize(40, 40);
        pane.setMinSize(40, 40);
        pane.setMaxSize(40, 40);

        return pane;
    }

    public JFXComboBox<Binding> makeBindingComboBox(int i, List<Binding> sortedBindings) {
        JFXComboBox<Binding> comboBox = new JFXComboBox<>(items);

        comboBox.setId(String.valueOf(i));
        comboBox.setCellFactory(c -> new ImageListCell());
        comboBox.setButtonCell(new ImageListCell());
        comboBox.setTooltip(new Tooltip("Select the binding for this color"));
        comboBox.getSelectionModel().selectedItemProperty().addListener(bindingChangeListener);
        comboBox.getSelectionModel().select(sortedBindings.get(i));

        return comboBox;
    }

    public void saveBindingLibrary(ActionEvent actionEvent) {
        for (int i = 0; i < vbox.getChildren().size(); i++) {
            Binding binding = null;
            int colorCode = 0;
            Node nodeOut = vbox.getChildren().get(i);
            if (nodeOut instanceof HBox) {
                for (Node nodeIn : ((HBox) nodeOut).getChildren()) {
                    if (nodeIn instanceof BorderPane) {
                        colorCode = Integer.parseInt(nodeIn.getId());
                    }
                    if (nodeIn instanceof JFXComboBox) {
                        binding = ((Binding) ((JFXComboBox) nodeIn).getSelectionModel().getSelectedItem());
                    }
                }
            }
            bindingMap.replace(colorCode, binding);
        }

        bindingFilter.setBindingsMap(bindingMap);
        filteredImage.redraw();
        Stage stage = (Stage) anchorpane.getScene().getWindow();
        stage.close();
    }

    public int getColorFromBinding(Binding binding) {
        for (int i = 0; i < vbox.getChildren().size(); i++) {
            int colorCode = 0;
            Node nodeOut = vbox.getChildren().get(i);
            if (nodeOut instanceof HBox) {
                for (Node nodeIn : ((HBox) nodeOut).getChildren()) {
                    if (nodeIn instanceof BorderPane) {
                        colorCode = Integer.parseInt(nodeIn.getId());
                    }
                    if (nodeIn instanceof JFXComboBox) {
                        if (binding == ((JFXComboBox) nodeIn).getSelectionModel().getSelectedItem()) {
                            return colorCode;
                        }
                    }
                }
            }
            bindingMap.replace(colorCode, binding);
        }
        return 0;
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

                setText("Binding");

            }
        }
    }
}