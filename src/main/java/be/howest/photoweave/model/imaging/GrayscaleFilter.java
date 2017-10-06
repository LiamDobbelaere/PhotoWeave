package be.howest.photoweave.model.imaging;

/**
 * Created by tomdo on 6/10/2017.
 */
public class GrayscaleFilter implements RGBFilter {
    @Override
    public int applyTo(int rgb) {
        int a = rgb & 0xff000000;
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        rgb = (r * 77 + g * 151 + b * 28) >> 8;

        return a | (rgb << 16) | (rgb << 8) | rgb;
    }
}
