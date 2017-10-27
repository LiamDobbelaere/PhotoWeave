package be.howest.photoweave.model.weaving;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingPalette;
import be.howest.photoweave.model.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class WovenImage {
    private BufferedImage sourceImage;
    private BufferedImage resultImage;

    private BindingPalette bindingPalette;

    private Integer markedBinding;
    private boolean showMarkedBinding;

    public WovenImage(BufferedImage sourceImage) {
        this.sourceImage = ImageUtil.convertImageToRGBInt(sourceImage);

        this.resultImage = ImageUtil.createBlankCopy(this.sourceImage);
        this.bindingPalette = new BindingPalette(this.sourceImage);
    }

    public void redraw() {
        Graphics gr = this.resultImage.getGraphics();
        gr.clearRect(0, 0, this.resultImage.getWidth(), this.resultImage.getHeight());
        gr.dispose();

        int[] imageData = ImageUtil.getDataBufferIntData(this.sourceImage);
        int[] targetData = ImageUtil.getDataBufferIntData(this.resultImage);

        tilingAlgorithm(imageData,targetData);
    }

    public boolean hasFloaters() {
        DataBufferInt dbb = (DataBufferInt) this.resultImage.getRaster().getDataBuffer();
        int[] targetData = dbb.getData();

        boolean hasFloaters = false;
        int colorCount = 0;
        int lastColor = 0;
        //Floaters checking
        for (int i = 0; i < targetData.length; i++) {
            int color = targetData[i];

            if (color == lastColor) colorCount++;
            else colorCount = 0;

            if (colorCount > 3) {
                hasFloaters = true;
            }

            lastColor = color;
        }

        return hasFloaters;
    }

    private void tilingAlgorithm(int[] imageData, int[] targetData){
        for (int i = 0; i < imageData.length; i++) {
            Binding binding = bindingPalette.getBindingPalette().get(imageData[i]);
            BufferedImage pattern = binding.getBindingImage();

            int x = (i % this.sourceImage.getWidth()) % pattern.getWidth();
            int y = ((int) Math.floor(i / this.sourceImage.getWidth())) % pattern.getHeight();
            int color = pattern.getRGB(x, y);

            if (showMarkedBinding && imageData[i] == markedBinding) {
                if (color == Color.BLACK.getRGB()) color = Color.RED.getRGB();
                else color = Color.LIGHT_GRAY.getRGB();
            }

            targetData[i] = color;
        }
    }

    /* Getter */
    public BufferedImage getResultImage() {
        return resultImage;
    }

    public void setMarkedBinding(Integer markedBinding) {
        this.markedBinding = markedBinding;
    }

    public void setShowMarkedBinding(boolean showMarkedBinding) {
        this.showMarkedBinding = showMarkedBinding;
    }

    public BindingPalette getBindingPalette() {
        return bindingPalette;
    }
}