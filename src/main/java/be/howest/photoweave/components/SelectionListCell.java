package be.howest.photoweave.components;


import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class SelectionListCell<T> extends ListCell<T> {
    private FilteredImage filteredImage;

    public SelectionListCell(FilteredImage filteredImage) {
        this.filteredImage = filteredImage;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            Region region = (Region) item;

            AnchorPane content = new AnchorPane();
            Label selectionInfo = new Label(String.format("%s, %s, %s, %s", region.getMinX(), region.getWidth(), region.getMinY(), region.getHeight()));
            JFXButton deleteButton = new JFXButton("Delete");
            deleteButton.getStyleClass().add("button-raised");

            content.getChildren().add(selectionInfo);
            content.getChildren().add(deleteButton);

            AnchorPane.setLeftAnchor(selectionInfo, 0.0);
            AnchorPane.setRightAnchor(deleteButton, 0.0);

            deleteButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Hello warudo");

                    ((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class)).removeRegion(region);

                    Platform.runLater(() -> {
                        filteredImage.redraw();
                    });
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
