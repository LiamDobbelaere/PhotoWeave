package be.howest.photoweave.components;

import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by tomdo on 25/10/2017.
 */
public class ColorListCell extends ListCell<Color> {
    private BorderPane pane = new BorderPane();

    @Override
    protected void updateItem(Color item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        setText(null);

        if (item != null) {
            pane.setStyle("-fx-background-color: " + String.format("#%02x%02x%02x", item.getRed(), item.getGreen(), item.getBlue()));
            pane.setMaxHeight(40);
            pane.setMaxWidth(40);
            setGraphic(pane);

        }
    }
}