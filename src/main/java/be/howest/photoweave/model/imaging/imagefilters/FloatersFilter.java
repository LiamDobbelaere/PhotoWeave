package be.howest.photoweave.model.imaging.imagefilters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by tomdo on 15/11/2017.
 */
public class FloatersFilter implements ImageFilter {
    private int floaterTresholdX;
    private int floaterTresholdY;
    private boolean enabled;

    public FloatersFilter(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFloaterTresholdX(int floaterTresholdX) {
        this.floaterTresholdX = floaterTresholdX;
    }

    public void setFloaterTresholdY(int floaterTresholdY) {
        this.floaterTresholdY = floaterTresholdY;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void applyTo(BufferedImage image) {
        if (!enabled) return;

        int lastColor = 0;
        int colorCount = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            colorCount = 0;

            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);

                if (color == lastColor) colorCount++;
                else colorCount = 0;


                if (colorCount > floaterTresholdX - 1) {
                    colorCount = 0;

                    color = Color.RED.getRGB();
                    image.setRGB(x, y, color);
                }

                lastColor = color;
            }
        }

        lastColor = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            colorCount = 0;

            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getRGB(x, y);

                if (color == lastColor) colorCount++;
                else colorCount = 0;

                if (colorCount > floaterTresholdY - 1) {
                    colorCount = 0;

                    color = Color.orange.getRGB();
                    image.setRGB(x, y, color);
                }

                lastColor = color;
            }
        }
    }
}
