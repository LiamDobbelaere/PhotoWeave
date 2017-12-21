package be.howest.photoweave.model.imaging.imagefilters;

import be.howest.photoweave.model.imaging.Filter;

import java.awt.image.BufferedImage;

/**
 * An ImageFilter applies changes to an image as a whole, it's slower, more expensive and not threaded.
 * However, ImageFilters can access any pixel at any given time, making them suitable for certain applications.
 */
public interface ImageFilter extends Filter {
    void applyTo(BufferedImage image);
}
