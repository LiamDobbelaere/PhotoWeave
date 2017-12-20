package be.howest.photoweave.model.customFile.data;

public class Scale {
    private int width;
    private int height;
    private double ratio;

    public Scale(int width, int height) {
        this.width = width;
        this.height = height;
        this.ratio = width/height;
    }
}
