package be.howest.photoweave.controllers;

import be.howest.photoweave.components.SelectBinding;
import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.components.events.BindingChangedEventHandler;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.PrimitiveUtil;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomdo on 7/12/2017.
 */
public class ChangeSelectionBinding {
    public SelectBinding selectBinding;
    public JFXButton buttonConfirm;

    private FilteredImage filteredImage;
    private BindingFilter bindingFilter;
    private Region region;

    private boolean editing;

    public void initialize(FilteredImage filteredImage, Region region, boolean editing) {
        this.filteredImage = filteredImage;
        this.bindingFilter = (BindingFilter) this.filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        this.region = region;
        this.editing = editing;

        region.setMarked(true);
        if (!editing) this.bindingFilter.addRegion(region);

        List<Integer> levels = new ArrayList<>();

        for (int y = region.getMinY(); y < region.getMinY() + region.getHeight(); y++) {
            for (int x = region.getMinX(); x < region.getMinX() + region.getWidth(); x++) {
                if (region.getRegion()[y - region.getMinY()][x - region.getMinX()]) {
                    byte[] metaData = PrimitiveUtil.decomposeIntToBytes(filteredImage.getMetaDataAt(x, y));

                    if (!levels.contains((int) metaData[0])) {
                        levels.add((int) metaData[0]);
                    }
                }
            }
        }


        Map<Integer, Binding> filteredMap = new HashMap<>();

        for (int level : levels) {
            filteredMap.put(level, bindingFilter.getBindingsMap().get(level));
        }

        this.selectBinding.setBindingsMap(filteredMap, bindingFilter);

        this.selectBinding
                .getComboBoxLevels()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    this.updatePosterizationLevel();
                });

        this.selectBinding.addEventHandler(BindingChanged.BINDING_CHANGED, new BindingChangedEventHandler() {
            @Override
            public void onBindingChanged() {
                updatePosterizationLevel();
            }
        });
        updatePosterizationLevel();
    }

    private void updatePosterizationLevel() {
        region.setTargetLevel(this.selectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem());
        region.setTargetBinding(this.selectBinding.getSelectedBinding());

        this.filteredImage.redraw();

    }

    public void confirmChanges(ActionEvent actionEvent) {
        Stage stage = (Stage) buttonConfirm.getScene().getWindow();
        stage.close();
    }

    public void cancelChanges(ActionEvent actionEvent) {
        if (!editing) bindingFilter.removeRegion(region);

        Stage stage = (Stage) buttonConfirm.getScene().getWindow();
        stage.close();
    }

    EventHandler<WindowEvent> getCloseEventHandler() {
        return event -> {
            cancelChanges(null);
        };
    }

}