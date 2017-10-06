package be.howest.photoweave.model.weaving;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.HashMap;

/**
 * Created by tomdo on 6/10/2017.
 */
public class WovenImage {
    private BufferedImage sourceImage;
    private BufferedImage resultImage;
    private HashMap<Integer, Integer> indexedColors;
    private BufferedImage[] patterns;
    //private int[][] patterns;

    public WovenImage(BufferedImage sourceImage) {
        if (sourceImage.getType() != BufferedImage.TYPE_INT_RGB)
        {
            BufferedImage convertedImg = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics graphics = convertedImg.getGraphics();
            graphics.drawImage(sourceImage, 0, 0, null);
            graphics.dispose();

            sourceImage = convertedImg;
        }

        this.sourceImage = sourceImage;
        this.resultImage = new BufferedImage(this.sourceImage.getWidth(), this.sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.indexedColors = new HashMap<>();

        indexColors();
    }

    private void indexColors() {
        this.indexedColors.clear();

        DataBufferInt dbb = (DataBufferInt) this.sourceImage.getRaster().getDataBuffer();
        int[] imageData = dbb.getData();

        for (int rgb : imageData) {
            if (!this.indexedColors.containsKey(rgb))
                this.indexedColors.put(rgb, this.indexedColors.size());
        }

        this.patterns = new BufferedImage[this.indexedColors.size()];

        //this.patterns = new int[this.indexedColors.size()][];
    }

    public BufferedImage getResultImage() {
        return resultImage;
    }

    public HashMap<Integer, Integer> getIndexedColors() {
        return indexedColors;
    }

    public void setPattern(int index, BufferedImage pattern) {
        if (pattern.getType() != BufferedImage.TYPE_INT_RGB)
        {
            BufferedImage convertedImg = new BufferedImage(pattern.getWidth(), pattern.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics graphics = convertedImg.getGraphics();
            graphics.drawImage(pattern, 0, 0, null);
            graphics.dispose();

            pattern = convertedImg;
        }

        this.patterns[index] = pattern; //((DataBufferInt) pattern.getRaster().getDataBuffer()).getData();
    }

    public void redraw() {
        Graphics gr = this.resultImage.getGraphics();
        gr.clearRect(0, 0, this.resultImage.getWidth(), this.resultImage.getHeight());
        gr.dispose();

        DataBufferInt dbi = (DataBufferInt) this.sourceImage.getRaster().getDataBuffer();
        int[] imageData = dbi.getData();

        DataBufferInt dbb = (DataBufferInt) this.resultImage.getRaster().getDataBuffer();
        int[] targetData = dbb.getData();

        for (int i = 0; i < imageData.length; i++) {
            BufferedImage pattern = this.patterns[this.indexedColors.get(imageData[i])];

            int x = (i % this.sourceImage.getWidth()) % pattern.getWidth();
            int y = ((int) Math.floor(i / this.sourceImage.getWidth())) % pattern.getHeight();

            targetData[i] = pattern.getRGB(x, y); //pattern[x + (y * 10)]; //pattern.getRGB(x, y); //agh[x + (y * 10)];
        }

        int lastValue = -1;
        int lastCount = 0;

        for (int i = 0; i < targetData.length; i++) {
            if (lastValue != targetData[i]) {
                lastValue = targetData[i];
                lastCount = 0;
            } else {
                lastCount++;

                if (lastCount > 3) {
                    targetData[i] = ~targetData[i];
                    lastCount = 0;
                    lastValue = -1;
                }
            }
        }
    }
}
