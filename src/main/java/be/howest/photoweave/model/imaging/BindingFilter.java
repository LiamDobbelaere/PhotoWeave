package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.binding.BindingPalette;

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

    @Override
    public int applyTo(int rgb, int i) {
        int currentLevel = (int) Math.floor(((rgb >> 16) & 0xff) / (255.0 / this.posterizeFilter.getLevelCount()));

        Binding binding = bindingFactory.getOptimizedBindings()[currentLevel];
        BufferedImage pattern = binding.getBindingImage(); //binding.getBindingImage();

        int x = (i % sourceWidth) % pattern.getWidth();
        int y = ((int) Math.floor(i / sourceWidth)) % pattern.getHeight();

        return pattern.getRGB(x, y);
    }
}
