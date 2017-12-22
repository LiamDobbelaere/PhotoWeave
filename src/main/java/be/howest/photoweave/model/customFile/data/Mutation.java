package be.howest.photoweave.model.customFile.data;

import java.util.List;

public class Mutation{
    private Scale scale;
    private int posterization;
    private List<BindingData> bindingpalette;
    private List<RegionData> regionData;

    public Mutation(Scale scale, int posterization, List<BindingData> bindingpalette, List<RegionData> regionData) {
        this.scale = scale;
        this.posterization = posterization;
        this.bindingpalette = bindingpalette;
        this.regionData = regionData;
    }

    public Scale getScale() {
        return scale;
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
}