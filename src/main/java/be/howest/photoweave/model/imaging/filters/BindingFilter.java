package be.howest.photoweave.model.imaging.filters;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.imaging.FilteredImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomdo on 9/11/2017.
 */
public class BindingFilter implements RGBFilter {
    private PosterizeFilter posterizeFilter;
    private BindingFactory bindingFactory;
    private Map<Integer, Binding> bindings;

    private boolean inverted;
    private boolean showMarkedBinding;
    private Binding markedBinding;

    private FilteredImage filteredImage;

    public BindingFilter(PosterizeFilter posterizeFilter, FilteredImage filteredImage) {
        this.bindingFactory = new BindingFactory();
        this.posterizeFilter = posterizeFilter;
        this.filteredImage = filteredImage;
        this.bindings = new HashMap<>();
    }

    public Map<Integer, Binding> getBindings() {
        return bindings;
    }

    private int findBestBindingForLevel(int level) {
        return level * ((bindingFactory.getOptimizedBindings().length - 1) / (this.posterizeFilter.getLevelCount() - 1));
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public void setShowMarkedBinding(boolean showMarkedBinding) {
        this.showMarkedBinding = showMarkedBinding;
    }

    public void setMarkedBinding(Binding markedBinding) {
        this.markedBinding = markedBinding;
    }

    @Override
    public int applyTo(int rgb, int i) {
        int currentLevel = (int) Math.floor(((rgb >> 16) & 0xff) / (255.0 / (this.posterizeFilter.getLevelCount() - 1)));

        Binding binding = this.bindings.computeIfAbsent(
                currentLevel, level -> bindingFactory.getOptimizedBindings()[findBestBindingForLevel(level)]);

        BufferedImage pattern = binding.getBindingImage(); //binding.getBindingImage();

        int x = (i % this.filteredImage.getModifiedImage().getWidth()) % pattern.getWidth();
        int y = ((int) Math.floor(i / this.filteredImage.getModifiedImage().getWidth())) % pattern.getHeight();
        int color = pattern.getRGB(x, y);


        if (inverted) {
            if (color== Color.BLACK.getRGB()) color = Color.WHITE.getRGB();
            else color = Color.BLACK.getRGB();
        }

        if (markedBinding != null && showMarkedBinding && binding == markedBinding) {
            if (color == Color.BLACK.getRGB()) color = Color.YELLOW.getRGB();
            else color = Color.LIGHT_GRAY.getRGB();
        }

        return color;
    }
}
