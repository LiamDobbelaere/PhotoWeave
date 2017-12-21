package be.howest.photoweave.model.customFile;

public class selectedBindingsParams {
    private int index;
    private boolean marked;
    private BindingParams item;
    private floatersParams floaters;

    public selectedBindingsParams(int index, boolean marked, BindingParams item, floatersParams floaters) {
        this.index = index;
        this.marked = marked;
        this.item = item;
        this.floaters = floaters;
    }
}
