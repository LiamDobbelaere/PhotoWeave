package be.howest.photoweave.model.util;


import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class ImageUtil {

    public ImageUtil(){

    }


    public static BufferedImage convertImageToRGBInt(BufferedImage image){
        if (image.getType() != BufferedImage.TYPE_INT_RGB)
        {
            BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics graphics = convertedImg.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            image = convertedImg;
        }

        return image;
    }


    public static BufferedImage convertImageToByteBinary(BufferedImage image){
        if (image.getType() != BufferedImage.TYPE_BYTE_BINARY)
        {
            BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics graphics = convertedImg.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            image = convertedImg;
        }

        return image;
    }

    public static BufferedImage createBlankCopy(BufferedImage image){
        return new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public static int[] getDataBufferIntData(BufferedImage image){
        DataBufferInt dbb = (DataBufferInt) image.getRaster().getDataBuffer();
        return dbb.getData();
    }

    public static Image resample(Image input, int scaleFactor) {
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
