package be.howest.photoweave.components;

import com.sun.javafx.fxml.builder.JavaFXImageBuilder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.*;

import java.awt.image.BufferedImage;

/**
 * Created by tomdo on 25/10/2017.
 */
public class ImageListCell extends ListCell<BufferedImage> {
    private ImageView iv = new ImageView();



    @Override
    protected void updateItem(BufferedImage item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        setText(null);

        if (item != null) {
            iv.setImage(resample(SwingFXUtils.toFXImage(item, null), 4));

            setGraphic(iv);

            //setText("Binding name goes here");

        }
    }



    private Image resample(Image input, int scaleFactor) {
        final int W = (int) input.getWidth();
        final int H = (int) input.getHeight();
        final int S = scaleFactor;

        WritableImage output = new WritableImage(
                W * S,
                H * S
        );

        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < S; dy++) {
                    for (int dx = 0; dx < S; dx++) {
                        writer.setArgb(x * S + dx, y * S + dy, argb);
                    }
                }
            }
        }

        return output;
    }
}
