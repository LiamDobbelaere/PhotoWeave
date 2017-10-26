package be.howest.photoweave.model.util;

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

    public static BufferedImage createBlankCopy(BufferedImage image){
        return new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public static int[] getDataBufferIntData(BufferedImage image){
        DataBufferInt dbb = (DataBufferInt) image.getRaster().getDataBuffer();
        return dbb.getData();
    }
}
