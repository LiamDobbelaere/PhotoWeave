package be.howest.photoweave.model.customFile.data;

import java.util.List;

public class Mutation{
    private int posterization, width, height;
    private List<BindingData> bindingpalette;
    private List<RegionData> regionData;

    public Mutation(int width, int height, int posterization, List<BindingData> bindingpalette, List<RegionData> regionData) {
        this.posterization = posterization;
        this.width = width;
        this.height = height;
        this.bindingpalette = bindingpalette;
        this.regionData = regionData;
    }

    public int getPosterization() {
        return posterization;
    }

    public List<BindingData> getBindingpalette() {
        return bindingpalette;
    }

    public List<RegionData> getRegionData() {
        return regionData;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}