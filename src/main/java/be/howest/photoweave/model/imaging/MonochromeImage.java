package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.weaving.WovenImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomdo on 5/10/2017.
 */
public class MonochromeImage {
    private BufferedImage originalImage;
    private BufferedImage modifiedImage;

    private List<RGBFilter> filters;
    private PosterizeFilter posterizeFilter;

    /**
     * Creates a grayscale, posterized version of an image from a source image.
     * @param originalImage The source image to use.
     */
    public MonochromeImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
        this.modifiedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        this.posterizeFilter = new PosterizeFilter();

        this.filters = new ArrayList<>();
        this.filters.add(new GrayscaleFilter());
        this.filters.add(this.posterizeFilter);

        this.setLevels(2);
    }

    /**
     * Redraws the modified image and applies the filters to it.
     */
    public void redraw() {
        Graphics graphics = this.modifiedImage.getGraphics();
        graphics.drawImage(originalImage, 0, 0, this.originalImage.getWidth(), this.originalImage.getHeight(), null);
        graphics.dispose();

        applyFilters();
    }

    /**
     * Returns the modified image, which is the original image with the filters applied.
     * Call redraw() to update the modified image. You have to call redraw() at least once after creating a MonochromeImage.
     * @return The modified source image.
     */
    public BufferedImage getModifiedImage() {
        return modifiedImage;
    }

    /***
     * Sets the amount of levels for the posterize filter.
     * @param levels The amount of levels to set.
     */
    public void setLevels(int levels) {
        this.posterizeFilter.setLevels(levels);
    }
    
    private void applyFilters() {
        DataBufferInt dbi = (DataBufferInt) this.modifiedImage.getRaster().getDataBuffer();
        int[] imageData = dbi.getData();

        for (int i = 0; i < imageData.length; i++) {
            int rgb = imageData[i];

            for (RGBFilter filter : filters) {
                rgb = filter.applyTo(rgb);
            }

            imageData[i] = rgb;
        }
    }
}
