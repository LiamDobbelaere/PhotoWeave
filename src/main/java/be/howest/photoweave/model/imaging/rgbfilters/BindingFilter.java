package be.howest.photoweave.model.imaging.rgbfilters;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomdo on 9/11/2017.
 */
public class BindingFilter implements RGBFilter {
    private PosterizeFilter posterizeFilter;
    private BindingFactory bindingFactory;
    private Map<Integer, Binding> bindingsMap;

    private boolean inverted;
    private boolean showMarkedBinding;
    private boolean manualAssign;
    private Binding markedBinding;

    private FilteredImage filteredImage;

    private List<Region> regions;

    public BindingFilter(PosterizeFilter posterizeFilter, FilteredImage filteredImage) {
        this.bindingFactory = BindingFactory.getInstance();
        this.posterizeFilter = posterizeFilter;
        this.filteredImage = filteredImage;
        this.bindingsMap = new HashMap<>();
        this.regions = new ArrayList<>();
    }

    public BindingFactory getBindingFactory() {
        return bindingFactory;
    }

    public Map<Integer, Binding> getBindingsMap() {
        return bindingsMap;
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

    public PosterizeFilter getPosterizeFilter() {
        return posterizeFilter;
    }

    public void addRegion(Region region) {
        this.regions.add(region);
    }

    public void removeRegion(Region region) {
        this.regions.remove(region);
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setManualAssign(boolean value) {
        this.manualAssign = value;

        if (value) {
            for (int i = 0; i < this.posterizeFilter.getLevelCount(); i++) {
                this.getBindingsMap().put(i, null);
            }
        }
    }

    @Override
    public int applyTo(int rgb, int i, byte[] imageMetaData) {
        int currentLevel = (int) Math.floor(((rgb >> 16) & 0xff) / (255.0 / (this.posterizeFilter.getLevelCount() - 1)));

        int fullX = i % this.filteredImage.getModifiedImage().getWidth();
        int fullY = ((int) Math.floor(i / this.filteredImage.getModifiedImage().getWidth()));

        Binding binding;

        if (manualAssign) {
            if (this.bindingsMap.get(currentLevel) != null) {
                binding = this.bindingsMap.get(currentLevel);
            } else {
                return rgb;
            }
        } else {
            binding = this.bindingsMap.computeIfAbsent(
                    currentLevel, level -> bindingFactory.getOptimizedBindings()[findBestBindingForLevel(level)]);
        }

        boolean markRegion = false;

        for (Region regionItem : this.regions) {
            boolean[][] region = regionItem.getRegion();

            if (fullX >= regionItem.getMinX() && fullY >= regionItem.getMinY() &&
                    fullX < regionItem.getMinX() + regionItem.getWidth() &&
                    fullY < regionItem.getMinY() + regionItem.getHeight()) {

                if (region[fullY - regionItem.getMinY()][fullX - regionItem.getMinX()]
                        && imageMetaData[0] == regionItem.getTargetLevel()) {

                    markRegion = regionItem.isMarked();

                    //Todo: the 1 should be whatever other binding is assigned to the region
                    if (regionItem.getTargetBinding() != null) {
                        binding = regionItem.getTargetBinding();
                    }
                }
            }
        }

        BufferedImage pattern = binding.getBindingImage(); //binding.getBindingImage();

        int x = fullX % pattern.getWidth();
        int y = fullY % pattern.getHeight();
        int color = pattern.getRGB(x, y);


        if (inverted) {
            if (color == Color.BLACK.getRGB()) color = Color.WHITE.getRGB();
            else color = Color.BLACK.getRGB();
        }

        if ((markedBinding != null && showMarkedBinding && binding == markedBinding) || markRegion) {
            if (color == Color.BLACK.getRGB()) color = Color.YELLOW.getRGB();
            else color = Color.LIGHT_GRAY.getRGB();
        }

        return color;
    }

    public void setBindingsMap(Map<Integer, Binding> bindingsMap) {
        this.bindingsMap = bindingsMap;
    }
}
