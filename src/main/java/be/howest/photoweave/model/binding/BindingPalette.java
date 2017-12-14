package be.howest.photoweave.model.binding;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BindingPalette {

    private BindingFactory bindingFactory;

    private HashMap<Integer, Binding> bindingPalette = new HashMap<>();
    private List<Integer> sortedColors = new ArrayList<>();
    private List<Binding> sortedBindings = new ArrayList<>();

    public BindingPalette(BufferedImage sourceImage) {
        extractColorsFromImage(sourceImage);    // > sortedColors
        loadInInternalBindings();               // > bindingFactory  > sortedBindings
        combineColorAndBinding();               //   sortedColors + sortedBindings
    }

    private void extractColorsFromImage(BufferedImage sourceImage) {
        DataBufferInt dbb = (DataBufferInt) sourceImage.getRaster().getDataBuffer();
        int[] imageData = dbb.getData();

        for (int rgb : imageData) {
            if (!sortedColors.contains(rgb))
                sortedColors.add(rgb);
        }

        sortedColors.sort(Collections.reverseOrder());
    }

    private void loadInInternalBindings() {
        bindingFactory = new BindingFactory();
        sortedBindings = bindingFactory.getSortedBindings();
    }

    private void combineColorAndBinding() {
        Integer size = sortedColors.size();
        Integer mutator = ((sortedBindings.size() - 1) / (size - 1));

        for (int i = 0; i < size; i++) {
            bindingPalette.put(sortedColors.get(i), sortedBindings.get(i * mutator));
        }
    }

    /* Getters */
    public HashMap<Integer, Binding> getBindingPalette() {
        return bindingPalette;
    }

    public List<Integer> getSortedColors() {
        return sortedColors;
    }

    public List<Binding> getSortedBindings() {
        return sortedBindings;
    }

    public void setSortedBindings(List<Binding> sortedBindings) {
        this.sortedBindings = sortedBindings;
    }
}
