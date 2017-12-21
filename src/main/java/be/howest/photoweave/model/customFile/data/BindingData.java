package be.howest.photoweave.model.customFile.data;

public class BindingData {
    private String name;
    private int index;
    private String base64;

    public BindingData(String name, int index, String base64) {
        this.name = name;
        this.index = index;
        this.base64 = base64;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getBase64() {
        return base64;
    }
}
