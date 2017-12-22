package be.howest.photoweave.model.customFile.data;

import java.awt.*;
import java.util.List;

public class RegionData {
    private BindingData bindingData;
    private List<Point> points;

    public RegionData(BindingData bindingData, List<Point> points) {
        this.bindingData = bindingData;
        this.points = points;
    }

    public BindingData getBindingData() {
        return bindingData;
    }

    public List<Point> getPoints() {
        return points;
    }
}
