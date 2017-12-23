package be.howest.photoweave.model.customFile.data;

public class UserInterfaceData {
    private boolean inverted, marked;
    private int bindingIndex, xFloater, yFloater;
    private double viewHeight, viewWidth, xScroll, yScroll;

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

    public double getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(double viewHeight) {
        this.viewHeight = viewHeight;
    }

    public double getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(double viewWidth) {
        this.viewWidth = viewWidth;
    }
}
