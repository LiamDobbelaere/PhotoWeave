package be.howest.photoweave.components;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.util.ImageUtil;
import be.howest.photoweave.model.weaving.WovenImage;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorBindingLinker {
    //GUI elements
    public ScrollPane scrollpane;
    public PixelatedImageView imageview;
    public VBox vbox;
    private WovenImage wovenimage;
    private BindingPalette maxBindingPalette;
    private BindingPalette bindingPalette;
    private List<Integer> sortedColors = new ArrayList<>();
    private List<Binding> sortedBindings = new ArrayList<>();
    private ObservableList<Binding> items = FXCollections.observableArrayList();
    private ObservableList<Integer> colorItems = FXCollections.observableArrayList();
    private ChangeListener<Binding> bindingChangeListener;

    public void initialize(FilteredImage wovenImage) {
        this.wovenimage = wovenImage;
        this.maxBindingPalette = wovenImage.getMaxBindingPalette();
        this.bindingPalette = wovenImage.getBindingPalette();
        this.sortedBindings = maxBindingPalette.getSortedBindings();
        this.sortedColors = maxBindingPalette.getSortedColors();

        this.vbox = new VBox();

        imageview.setImage(SwingFXUtils.toFXImage(wovenImage.getResultImage(), null));

        bindingChangeListener = new ChangeListener<Binding>() {
            @Override
            public void changed(ObservableValue<? extends Binding> observable, Binding oldValue, Binding newValue) {

                //comboBox.fireEvent(new BindingChanged());
            }
        };

        items.clear();
        items.addAll(this.sortedBindings);

        colorItems.clear();
        colorItems.addAll(this.maxBindingPalette.getBindingPalette().keySet());

        for (int i = 0; i < colorItems.size(); i++) {
            if (bindingPalette.getSortedColors().contains(colorItems.get(i))) {
                HBox hbox = new HBox();

                hbox.getChildren().add(makeColorPalet(colorItems.get(i)));
                hbox.getChildren().add(makeBindingComboBox(i));

                vbox.getChildren().add(hbox);
            }
        }
        scrollpane.setContent(vbox);
    }

    public BorderPane makeColorPalet(Integer colorCode) {

        BorderPane pane = new BorderPane();
        Color color = new Color(colorCode);

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
        comboBox.getSelectionModel().select(i * ((sortedBindings.size() - 1) / (sortedColors.size() - 1)));

        return comboBox;
    }

    public void saveBindingLibrary(ActionEvent actionEvent) {

        List<Binding> bindings = new ArrayList<>();

        for (int i = 0; i < vbox.getChildren().size(); i++) {
            Node nodeOut = vbox.getChildren().get(i);
            if (nodeOut instanceof HBox) {
                for (Node nodeIn : ((HBox) nodeOut).getChildren()) {
                    if (nodeIn instanceof BorderPane) {
                        System.out.println(((BorderPane) nodeIn).getBackground().getFills().get(0).getFill().toString());
                        if (maxBindingPalette.getSortedColors().contains(((BorderPane) nodeIn).getBackground().getFills().get(0))) {
                            System.out.println("in");
                        }
                    }
                    if (nodeIn instanceof JFXComboBox) {


                        bindings.add(((Binding) ((JFXComboBox) nodeIn).getSelectionModel().getSelectedItem()));
                        System.out.println("yes" + i);
                    }
                }
            }
        }

        maxBindingPalette.setSortedBindings(bindings);
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