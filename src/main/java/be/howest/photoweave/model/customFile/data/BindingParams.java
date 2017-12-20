package be.howest.photoweave.model.customFile.data;

import be.howest.photoweave.model.customFile.selectedBindingsParams;

public class BindingParams {
    private boolean inverted;
    private selectedBindingsParams selectedbinding;

    public BindingParams(boolean inverted, selectedBindingsParams selectedbinding) {
        this.inverted = inverted;
        this.selectedbinding = selectedbinding;
    }
}
