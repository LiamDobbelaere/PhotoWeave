package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.imaging.imagefilters.ImageFilter;
import be.howest.photoweave.model.imaging.rgbfilters.RGBFilter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tomdo on 11/11/2017.
 */
public class FilterList {
    private ArrayList<RGBFilter> rgbFilters;
    private ArrayList<ImageFilter> imageFilters;

    public FilterList() {
        this.rgbFilters = new ArrayList<>();
        this.imageFilters = new ArrayList<>();
    }

    public void add(RGBFilter filter) {
        this.rgbFilters.add(filter);
    }
    public void add(ImageFilter filter) {
        this.imageFilters.add(filter);
    }

    public void remove(RGBFilter filter) {
        this.rgbFilters.remove(filter);
    }
    public void remove(ImageFilter filter) {
        this.imageFilters.remove(filter);
    }

    public RGBFilter findRGBFilter(Class<? extends RGBFilter> searchedFilter) {
        RGBFilter found = null;

        for (RGBFilter filter : rgbFilters) {
            if (searchedFilter.isInstance(filter)) {
                found = filter;
            }
        }

        return found;
    }

    public ImageFilter findImageFilter(Class<? extends ImageFilter> searchedFilter) {
        ImageFilter found = null;

        for (ImageFilter filter : imageFilters) {
            if (searchedFilter.isInstance(filter)) {
                found = filter;
            }
        }

        return found;
    }

    public void resize(int newWidth, int newHeight) {
        for (RGBFilter filter : this.rgbFilters) {
            filter.resize(newWidth, newHeight);
        }

        for (ImageFilter filter : this.imageFilters) {
            filter.resize(newWidth, newHeight);
        }
    }

    public Iterator<RGBFilter> getRGBFilters() {
        return rgbFilters.iterator();
    }
    public Iterator<ImageFilter> getImageFilters() { return imageFilters.iterator(); }
}
