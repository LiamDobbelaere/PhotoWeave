package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.imaging.imagefilters.ImageFilter;
import be.howest.photoweave.model.imaging.rgbfilters.RGBFilter;
import be.howest.photoweave.model.util.ImageUtil;
import be.howest.photoweave.model.util.PrimitiveUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilteredImage {
    private BufferedImage originalImage;
    private BufferedImage modifiedImage;

    private FilterList filters = new FilterList();

    int threadCount = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
    int threadsDone = 0;

    private Thread[] threads;
    private List<ThreadEventListener> threadEventListeners;

    private int[] imageMetaData;

    /**
     * Creates a grayscale, posterized version of an image from a source image.
     *
     * @param originalImage The source image to use.
     */
    public FilteredImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
        this.modifiedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        this.filters = new FilterList();

        this.threadEventListeners = new ArrayList<>();
    }

    public void resize(int newWidth, int newHeight) {
        this.filters.resize(newWidth, newHeight);
        this.modifiedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Redraws the modified image and applies the rgbfilters to it.
     */
    public void redraw() {
        this.threadEventListeners.forEach(ThreadEventListener::OnRedrawBegin);

        Graphics graphics = this.modifiedImage.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, this.modifiedImage.getWidth(), this.modifiedImage.getHeight());
        graphics.drawImage(originalImage, 0, 0, this.modifiedImage.getWidth(), this.modifiedImage.getHeight(), null);
        graphics.dispose();

        applyFilters();
    }

    /**
     * Returns the modified image, which is the original image with the rgbfilters applied.
     * Call redraw() to update the modified image. You have to call redraw() at least once after creating a FilteredImage.
     *
     * @return The modified source image.
     */
    public BufferedImage getModifiedImage() {
        return modifiedImage;
    }

    public int getMetaDataAt(int x, int y) {
        return imageMetaData[x + (this.modifiedImage.getWidth() * y)];
    }

    private void applyFilters() {
        int[] imageData = ImageUtil.getDataBufferIntData(this.modifiedImage);
        imageMetaData = new int[imageData.length];

        //Interrupt all existing jobs
        if (threads != null) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }

        /*
        The following code will break the array into smaller pieces depending on the number of threads
        This is to take away the slowdown that happens if two threads try to modify the same array,
        so now they get their own smaller version to work with instead, and copy it back into the original at the end
        */

        threads = new Thread[threadCount];

        int[][] pieces = new int[threadCount][];
        int[][] piecesMeta = new int[threadCount][];

        threadsDone = 0;

        for (int k = 0; k < threadCount; k++) {
            int start = ((imageData.length - 1) / threadCount) * k;
            int end;

            if (k == threadCount - 1)
                end = imageData.length;
            else
                end = ((imageData.length - 1) / threadCount) * (k + 1);

            pieces[k] = new int[end - start];
            piecesMeta[k] = new int[end - start];

            System.arraycopy(imageData, start, pieces[k], 0, end - start);
            //We don't copy metadata because it's all 0

            int[] ref = pieces[k];
            int[] refMeta = piecesMeta[k];

            threads[k] = new Thread(() -> applyFilterThreaded(ref, refMeta, imageData, imageMetaData, start));
            threads[k].start();
        }

        System.out.println("FilteredImage redrawn");
    }

    private void applyFilterThreaded(int[] imageData, int[] imageMetaData, int[] fullImageData, int[] fullImageMetaData,
                                     int actualStart) {
        for (int i = 0; i < imageData.length; i++) {
            int rgb = imageData[i];

            Iterator<RGBFilter> filterIterator = filters.getRGBFilters();

            while (filterIterator.hasNext()) {
                byte[] decomposedMetaData = PrimitiveUtil.decomposeIntToBytes(imageMetaData[i]);

                RGBFilter filter = filterIterator.next();
                rgb = filter.applyTo(rgb, actualStart + i, decomposedMetaData);

                imageMetaData[i] = PrimitiveUtil.composeIntFromBytes(decomposedMetaData);
            }

            imageData[i] = rgb;

            if (Thread.currentThread().isInterrupted()) i = imageData.length;
        }

        synchronized (this) {
            if (!Thread.currentThread().isInterrupted()) {
                System.arraycopy(imageData, 0, fullImageData, actualStart, imageData.length);
                System.arraycopy(imageMetaData, 0, fullImageMetaData, actualStart, imageData.length);
                threadsDone++;
                notifyThreadEventListeners();
            }
        }
    }

    private void applyImageFilters() {
        Iterator<ImageFilter> imageFilterIterator = this.filters.getImageFilters();

        while (imageFilterIterator.hasNext()) {
            ImageFilter imageFilter = imageFilterIterator.next();
            imageFilter.applyTo(this.modifiedImage);
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

        if (threadsDone >= threadCount) {
            applyImageFilters();
            this.threadEventListeners.forEach(ThreadEventListener::onRedrawComplete);
        }
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public FilterList getFilters() {
        return filters;
    }
}
