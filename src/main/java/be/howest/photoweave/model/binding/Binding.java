package be.howest.photoweave.model.binding;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;

//TODO add name of file -> in BindingLib this will link the bindings
//TODO add folder -> in BindingLib this will be the library
public class Binding {
    private BufferedImage bindingImage;
    private String name;

    public Binding(InputStream is, String name) throws IOException {
        this.bindingImage = ImageIO.read(is);
        this.name = name;
    }

    //A copy of the original bindingImage will be returned with the given color
    //Only for UI purposes
    public BufferedImage getImageWithColor(Color color) {
        return null; //TODO
    }

    public Integer getIntensityCount(){
        DataBufferInt dbb = (DataBufferInt) this.getBindingImage().getRaster().getDataBuffer();
        int[] targetData = dbb.getData();

        int colorCount = 0;
        int checkColor = -16777216; //BLACK

        for (int i = 0; i < targetData.length; i++) {
            int color = targetData[i];

            if (color == checkColor) colorCount++;
        }

        return colorCount;
    }

    /* Getters en Setters*/
    public BufferedImage getBindingImage() {
        return bindingImage;
    }

    public void setBindingImage(BufferedImage bindingImage) {
        this.bindingImage = bindingImage;
    }

    public String getName() {
        return name;
    }
}
