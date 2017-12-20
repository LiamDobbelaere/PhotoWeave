package be.howest.photoweave.model.customFile.data;

public class ImageData {
    private String base64;
    private int width;
    private int height;

    public ImageData(String base64, int width, int height) {
        this.base64 = base64;
        this.width = width;
        this.height = height;
    }
}
