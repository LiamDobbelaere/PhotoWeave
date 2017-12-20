package be.howest.photoweave.model.customFile.data;

import java.util.List;

public class Mutation{
    private Scale scale;
    private int posterization;
    private List<BindingData> bindingpalette;

    public Mutation(Scale scale, int posterization, List<BindingData> bindingpalette) {
        this.scale = scale;
        this.posterization = posterization;
        this.bindingpalette = bindingpalette;
    }
}