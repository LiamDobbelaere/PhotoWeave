package be.howest.photoweave.model.customFile.data;

public class UserInterfaceData {
    private boolean inverted, marked;
    private int bindingIndex, xFloater, yFloater;
    private double height, width, xScroll, yScroll;

    public UserInterfaceData() {
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getBindingIndex() {
        return bindingIndex;
    }

    public void setBindingIndex(int bindingIndex) {
        this.bindingIndex = bindingIndex;
    }

    public int getxFloater() {
        return xFloater;
    }

    public void setxFloater(int xFloater) {
        this.xFloater = xFloater;
    }

    public int getyFloater() {
        return yFloater;
    }

    public void setyFloater(int yFloater) {
        this.yFloater = yFloater;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getxScroll() {
        return xScroll;
    }

    public void setxScroll(double xScroll) {
        this.xScroll = xScroll;
    }

    public double getyScroll() {
        return yScroll;
    }

    public void setyScroll(double yScroll) {
        this.yScroll = yScroll;
    }
}
