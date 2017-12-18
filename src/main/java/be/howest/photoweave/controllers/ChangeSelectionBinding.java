package be.howest.photoweave.controllers;

import be.howest.photoweave.components.SelectBinding;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.PrimitiveUtil;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
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

    public void initialize(FilteredImage filteredImage, Region region) {
        this.filteredImage = filteredImage;
        this.bindingFilter = (BindingFilter) this.filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        this.region = region;

        region.setMarked(true);
        this.bindingFilter.addRegion(region);

        List<Integer> levels = new ArrayList<>();

        for (int y = 0; y < region.getHeight(); y++) {
            for (int x = 0; x < region.getWidth(); x++) {
                int actualX = x + region.getMinX();
                int actualY = y + region.getMinY();

                byte[] metaData = PrimitiveUtil.decomposeIntToBytes(filteredImage.getMetaDataAt(actualX, actualY));

                levels.add((int) metaData[0]);
            }
        }

        Map<Integer, Binding> filteredMap = new HashMap<>();

        for (int level : levels) {
            filteredMap.put(level, bindingFilter.getBindingsMap().get(level));
        }

        this.selectBinding.setBindingsMap(filteredMap);


        //
        /*vboxSelectBinding
                .getComboBoxBindings()
                .addEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);
        */

        this.selectBinding
                .getComboBoxLevels()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    this.updatePosterizationLevel();
                });

        /*this.selectBinding
                .getComboBoxBindings()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    this.updatePosterizationLevel();
                });*/

        this.filteredImage.redraw();
    }

    private void updatePosterizationLevel() {
        region.setTargetLevel(this.selectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem());
        //region.setTargetBinding(this.selectBinding.getComboBoxBindings().getSelectionModel().getSelectedItem());

        this.filteredImage.redraw();

    }

    public void confirmChanges(ActionEvent actionEvent) {
        Stage stage = (Stage) buttonConfirm.getScene().getWindow();
        stage.close();
    }

    public void cancelChanges(ActionEvent actionEvent) {
        bindingFilter.removeRegion(region);

        Stage stage = (Stage) buttonConfirm.getScene().getWindow();
        stage.close();
    }

    EventHandler<WindowEvent> getCloseEventHandler() {
        return event -> {
            cancelChanges(null);
        };
    }

}