package be.howest.photoweave.components;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXListCell;
import com.sun.javafx.fxml.builder.JavaFXImageBuilder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.*;

import java.awt.image.BufferedImage;

/**
 * Created by tomdo on 25/10/2017.
 */
public class ImageListCell extends JFXListCell<Binding> {
    private ImageView iv = new ImageView();



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
