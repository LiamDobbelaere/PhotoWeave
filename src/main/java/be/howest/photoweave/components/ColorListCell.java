package be.howest.photoweave.components;

import javafx.scene.control.ListCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;

import java.awt.*;

/**
 * Created by tomdo on 25/10/2017.
 */
public class ColorListCell extends ListCell<Integer> {
    private BorderPane pane = new BorderPane();

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        setText(null);

        if (item != null) {
            Color color = new Color(item);

            pane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
            pane.setPrefSize(40, 40);
            pane.setMinSize(40, 40);
            pane.setMaxSize(40, 40);

            setGraphic(pane);

        }
    }
}