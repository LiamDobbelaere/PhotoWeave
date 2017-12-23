package be.howest.photoweave.model.imaging.rgbfilters;

/**
 * Created by tomdo on 6/10/2017.
 */
public class PosterizeFilter implements RGBFilter {
    private int[] levels;
    private int levelCount;

    public PosterizeFilter() {
        this.setLevels(2);
    }

    public void setLevels(int levelCount) {
        this.levelCount = levelCount;

        levels = new int[256];
        if (levelCount != 1)
            for (int i = 0; i < 256; i++)
                levels[i] = 255 * (levelCount*i / 256) / (levelCount-1);
    }

    public int getLevelCount() {
        return this.levelCount;
    }

    @Override
    public int applyTo(int rgb, int i, byte[] imageMetaData) {
        int a = rgb & 0xff000000;
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        r = levels[r];
        g = levels[g];
        b = levels[b];

        imageMetaData[0] = (byte) Math.floor(r / (255.0 / (levelCount - 1)));

        return a | (r << 16) | (g << 8) | b;
    }

    @Override
    public void resize(int newWidth, int newHeight) {

    }

}
