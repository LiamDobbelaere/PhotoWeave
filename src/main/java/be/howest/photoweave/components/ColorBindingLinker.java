package be.howest.photoweave.components;

import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.ThreadEventListener;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorBindingLinker implements ThreadEventListener {
    //GUI elements
    public AnchorPane anchorpane;
    public ScrollPane scrollpane;
    public PixelatedImageView imageview;
    public VBox vbox;

    private FilteredImage filteredImage;
    private BindingFilter bindingFilter;

    private Map<Integer, Binding> mapBackup;

    public void initialize(FilteredImage filteredImage) {
        this.filteredImage = filteredImage;
        this.filteredImage.addThreadEventListener(this);

        imageview.setImage(SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null));

        this.bindingFilter = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        mapBackup = new HashMap<>(this.bindingFilter.getBindingsMap());

        this.vbox = new VBox();

        for (Integer key : this.bindingFilter.getBindingsMap().keySet()) {
            try {
                SelectBinding selectBinding = new SelectBinding();

                Map<Integer, Binding> bindingMap = new HashMap<>();
                bindingMap.put(key, this.bindingFilter.getBindingsMap().get(key));

                selectBinding.setBindingsMap(bindingMap, this.bindingFilter);

                selectBinding.addEventHandler(BindingChanged.BINDING_CHANGED, new EventHandler<BindingChanged>() {
                    @Override
                    public void handle(BindingChanged event) {
                        bindingFilter.getBindingsMap().put(selectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem(), selectBinding.getSelectedBinding());

                        filteredImage.redraw();
                    }
                });

                vbox.getChildren().add(selectBinding);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scrollpane.setContent(vbox);

        filteredImage.redraw();
    }

    public void saveBindingLibrary(ActionEvent actionEvent) {
        bindingFilter.setManualAssign(false);
        filteredImage.redraw();

        Stage stage = (Stage) anchorpane.getScene().getWindow();
        stage.close();
    }

    @Override
    public void OnRedrawBegin() {

    }

    @Override
    public void onThreadComplete() {

    }

    @Override
    public void onRedrawComplete() {
        imageview.setImage(SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null));
    }

    public void cancel(ActionEvent actionEvent) {
        bindingFilter.setManualAssign(false);

        for (Integer key : mapBackup.keySet()) {
            bindingFilter.getBindingsMap().put(key, mapBackup.get(key));
        }

        filteredImage.redraw();

        Stage stage = (Stage) anchorpane.getScene().getWindow();
        stage.close();
    }
}