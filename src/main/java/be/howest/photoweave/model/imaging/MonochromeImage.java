package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.binding.BindingPalette;
import be.howest.photoweave.model.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
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

        BindingFilter bindingFilter = new BindingFilter(this.posterizeFilter, this.modifiedImage.getWidth(), this.modifiedImage.getHeight());

        this.filters = new ArrayList<>();
        this.filters.add(new GrayscaleFilter());
        this.filters.add(this.posterizeFilter);
        this.filters.add(bindingFilter);

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
        int[] imageData = ImageUtil.getDataBufferIntData(this.modifiedImage);

        int threadCount = 8;
        Thread[] threads = new Thread[threadCount];

        for (int k = 0; k < threadCount; k++) {
            int start = ((imageData.length - 1) / threadCount) * k;
            int end = ((imageData.length - 1) / threadCount) * (k + 1);

            threads[k] = new Thread(() -> applyFilterThreaded(imageData, start, end));
            threads[k].start();
        }

        for (int k = 0; k < threadCount; k++)
            try {
                threads[k].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

    private void applyFilterThreaded(int[] imageData, int start, int end) {
        System.out.println("Start: " + String.valueOf(start) + ", End: " + String.valueOf(end));

        for (int i = start; i < end; i++) {
            int rgb = imageData[i];

            for (RGBFilter filter : filters) {
                rgb = filter.applyTo(rgb, i);
            }

            imageData[i] = rgb;
        }
    }
}
