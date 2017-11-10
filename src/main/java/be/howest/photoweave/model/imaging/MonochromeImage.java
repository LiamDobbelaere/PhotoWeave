package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.imaging.filters.BindingFilter;
import be.howest.photoweave.model.imaging.filters.GrayscaleFilter;
import be.howest.photoweave.model.imaging.filters.PosterizeFilter;
import be.howest.photoweave.model.imaging.filters.RGBFilter;
import be.howest.photoweave.model.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
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
    private BindingFilter bindingFilter;

    int threadCount = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
    int threadsDone = 0;

    private Thread[] threads;

    boolean stopWriting = false;

    private List<ThreadEventListener> threadEventListeners;

    /**
     * Creates a grayscale, posterized version of an image from a source image.
     * @param originalImage The source image to use.
     */
    public MonochromeImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
        this.modifiedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        this.posterizeFilter = new PosterizeFilter();

        bindingFilter = new BindingFilter(this.posterizeFilter, this.modifiedImage.getWidth(), this.modifiedImage.getHeight());

        this.filters = new ArrayList<>();
        this.filters.add(new GrayscaleFilter());
        this.filters.add(this.posterizeFilter);
        //this.filters.add(bindingFilter);

        this.setLevels(2);

        this.threadEventListeners = new ArrayList<>();
    }

    public void resize(int newWidth, int newHeight) {
        this.modifiedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        bindingFilter.setSourceWidth(newWidth);
        bindingFilter.setSourceHeight(newHeight);
    }

    /**
     * Redraws the modified image and applies the filters to it.
     */
    public void redraw() {
        Graphics graphics = this.modifiedImage.getGraphics();
        graphics.drawImage(originalImage, 0, 0, this.modifiedImage.getWidth(), this.modifiedImage.getHeight(), null);
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

        threads = new Thread[threadCount];
        int[][] pieces = new int[threadCount][];

        threadsDone = 0;

        for (int k = 0; k < threadCount; k++) {
            int start = ((imageData.length - 1) / threadCount) * k;
            int end;

            if (k == threadCount - 1)
                end = imageData.length;
            else
                end = ((imageData.length - 1) / threadCount) * (k + 1);

            pieces[k] = new int[end - start];

            System.arraycopy(imageData, start, pieces[k], 0, end - start);

            int[] ref = pieces[k];

            threads[k] = new Thread(() -> applyFilterThreaded(ref, imageData, start));
            threads[k].start();
        }
    }

    //private void applyFilterThreaded(int[] imageData, int start, int end) {
    private void applyFilterThreaded(int[] imageData, int[] fullImageData, int actualStart) {
        for (int i = 0; i < imageData.length; i++) {
            int rgb = imageData[i];

            for (RGBFilter filter : filters) {
                rgb = filter.applyTo(rgb, actualStart + i);
            }

            imageData[i] = rgb;
        }

        synchronized (this) {
            System.arraycopy(imageData, 0, fullImageData, actualStart, imageData.length);
            threadsDone++;
            notifyThreadEventListeners();
        }
    }

    public void addThreadEventListener(ThreadEventListener t) {
        this.threadEventListeners.add(t);
    }

    public void removeThreadEventListener(ThreadEventListener t) {
        this.threadEventListeners.remove(t);
    }

    private void notifyThreadEventListeners() {
        this.threadEventListeners.forEach(ThreadEventListener::onThreadComplete);

        if (threadsDone == threadCount)
            this.threadEventListeners.forEach(ThreadEventListener::onRedrawComplete);
    }
}
