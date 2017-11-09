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

    private Binding test;

    public BindingFilter(PosterizeFilter posterizeFilter, int sourceWidth, int sourceHeight) {
        this.test = new BindingFactory().getSortedBindings().get(0);

        this.posterizeFilter = posterizeFilter;

        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    @Override
    public int applyTo(int rgb, int i) {
        //Binding binding = bindingPalette.getBindingPalette().get(rgb);
        BufferedImage pattern = this.test.getBindingImage(); //binding.getBindingImage();

        int x = (i % sourceWidth) % pattern.getWidth();
        int y = ((int) Math.floor(i / sourceHeight)) % pattern.getHeight();

        return pattern.getRGB(x, y);
    }
}
