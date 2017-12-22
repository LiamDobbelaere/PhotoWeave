package be.howest.photoweave.components;


import be.howest.photoweave.controllers.EditPhoto;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

public class SelectionListCell<T> extends ListCell<T> {
    private FilteredImage filteredImage;
    private EditPhoto editPhoto;

    public SelectionListCell(FilteredImage filteredImage, EditPhoto editPhoto) {
        this.filteredImage = filteredImage;
        this.editPhoto = editPhoto;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            Region region = (Region) item;

            AnchorPane content = new AnchorPane();
            Label selectionInfo = new Label(String.format("%s, %s, %s, %s", region.getMinX(), region.getWidth(), region.getMinY(), region.getHeight()));
            HBox buttonsContainer = new HBox();

            JFXButton deleteButton = new JFXButton("Delete");
            deleteButton.getStyleClass().add("button-raised");
            deleteButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            deleteButton.setAlignment(Pos.CENTER);
            deleteButton.setTextAlignment(TextAlignment.CENTER);
            deleteButton.setMinWidth(30.0);
            deleteButton.setTooltip(new Tooltip("Verwijder selectie"));
            SVGPath deleteIcon = new SVGPath();
            deleteIcon.setFill(Paint.valueOf("WHITE"));
            deleteIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
            deleteButton.setGraphic(deleteIcon);

            JFXButton editButton = new JFXButton("Edit");
            editButton.getStyleClass().add("button-raised");
            editButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            editButton.setAlignment(Pos.CENTER);
            editButton.setTextAlignment(TextAlignment.CENTER);
            editButton.setMinWidth(30.0);
            editButton.setTooltip(new Tooltip("Bewerk selectie"));
            SVGPath editIcon = new SVGPath();
            editIcon.setFill(Paint.valueOf("WHITE"));
            editIcon.setContent("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
            editButton.setGraphic(editIcon);

            buttonsContainer.getChildren().add(editButton);
            buttonsContainer.getChildren().add(deleteButton);

            content.getChildren().add(selectionInfo);
            content.getChildren().add(buttonsContainer);

            AnchorPane.setLeftAnchor(selectionInfo, 0.0);
            AnchorPane.setRightAnchor(buttonsContainer, 0.0);

            deleteButton.addEventHandler(ActionEvent.ACTION, event -> {
                ((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class)).removeRegion(region);

                filteredImage.redraw();
            });

            editButton.addEventHandler(ActionEvent.ACTION, event -> {
                try {
                    this.editPhoto.showChangeSelectionBindingWindow(region);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            setGraphic(content);


            //setText(String.format("%s, %s, %s, %s", region.getMinX(), region.getWidth(), region.getMinY(), region.getHeight()));
        } else {
            //setText(null);
            setGraphic(null);
        }
    }
}
