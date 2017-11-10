package be.howest.photoweave.model.imaging.filters;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;

import java.awt.image.BufferedImage;

/**
 * Created by tomdo on 9/11/2017.
 */
public class BindingFilter implements RGBFilter {
    private int sourceWidth;
    private int sourceHeight;
    private PosterizeFilter posterizeFilter;

    private BindingFactory bindingFactory;

    public BindingFilter(PosterizeFilter posterizeFilter, int sourceWidth, int sourceHeight) {
        this.bindingFactory = new BindingFactory();
        this.posterizeFilter = posterizeFilter;

        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    public void setSourceWidth(int sourceWidth) {
        this.sourceWidth = sourceWidth;
    }

    public void setSourceHeight(int sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    @Override
    public int applyTo(int rgb, int i) {
        int currentLevel = (int) Math.floor(((rgb >> 16) & 0xff) / (255.0 / (this.posterizeFilter.getLevelCount() - 1)));

        int assignedBinding = (int) Math.round(
                (double) currentLevel *
                (((double) bindingFactory.getOptimizedBindings().length - 1) / ((double) this.posterizeFilter.getLevelCount() - 1))
        );

        Binding binding = bindingFactory.getOptimizedBindings()[assignedBinding];
        BufferedImage pattern = binding.getBindingImage(); //binding.getBindingImage();

        int x = (i % sourceWidth) % pattern.getWidth();
        int y = ((int) Math.floor(i / sourceWidth)) % pattern.getHeight();

        return pattern.getRGB(x, y);
    }
}
