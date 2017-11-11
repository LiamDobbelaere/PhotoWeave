package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.imaging.filters.BindingFilter;
import be.howest.photoweave.model.imaging.filters.RGBFilter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tomdo on 11/11/2017.
 */
public class FilterList {
    private ArrayList<RGBFilter> filters;

    public FilterList() {
        this.filters = new ArrayList<>();
    }

    public void add(RGBFilter filter) {
        this.filters.add(filter);
    }

    public void remove(RGBFilter filter) {
        this.filters.remove(filter);
    }

    public RGBFilter find(Class<? extends RGBFilter> searchedFilter) {
        RGBFilter found = null;

        for (RGBFilter filter : filters) {
            if (searchedFilter.isInstance(filter)) {
                found = filter;
            }
        }

        return found;
    }

    public Iterator<RGBFilter> getAll() {
        return filters.iterator();
    }
}
