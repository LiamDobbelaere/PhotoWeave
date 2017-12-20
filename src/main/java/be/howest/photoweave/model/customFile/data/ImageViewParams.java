package be.howest.photoweave.model.customFile.data;

public class ImageViewParams {
    private double zoom;
    private int scrollX;
    private int scrollY;

    public ImageViewParams(double zoom, int scrollX, int scrollY) {
        this.zoom = zoom;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }
}
