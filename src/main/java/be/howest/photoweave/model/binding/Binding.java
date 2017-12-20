package be.howest.photoweave.model.binding;

import be.howest.photoweave.model.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
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

    public Binding(String b64Image, String name) throws IOException {
        /* Image to B64
                Image image = ImageIO.read(new URL("https://i.imgur.com/05BNKi9.jpg"));
        BufferedImage bi = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bi.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(bi,"bmp",output);
        String test = DatatypeConverter.printBase64Binary(output.toByteArray());
        */
        byte[] b64ImageBytes = DatatypeConverter.parseBase64Binary(b64Image);
        this.bindingImage = ImageUtil.convertImageToRGBInt(ImageIO.read(new ByteArrayInputStream(b64ImageBytes)));
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
        int checkColor = Color.WHITE.getRGB();

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
