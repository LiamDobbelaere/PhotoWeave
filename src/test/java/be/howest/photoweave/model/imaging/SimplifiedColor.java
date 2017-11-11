package be.howest.photoweave.model.imaging;

/**
 * Created by tomdo on 11/11/2017.
 */
public class SimplifiedColor {
    private int rgb;

    public SimplifiedColor(int rgb) {
        this.rgb = rgb;
    }

    @Override
    public String toString() {
        int a = rgb & 0xff000000;
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        return String.format("(r%s, g%s, b%s, a%s)", r, g, b, a);
    }
}
