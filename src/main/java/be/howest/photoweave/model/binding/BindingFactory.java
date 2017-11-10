package be.howest.photoweave.model.binding;

import be.howest.photoweave.model.util.ImageUtil;

import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BindingFactory {

    private List<Binding> bindings = new ArrayList<>();
    private final Integer MAX_INTERNAL_BINDINGS = 24;
    private Binding[] optimizedBindings;

    public BindingFactory() {
        try {
            getBindingsFromInternalResources();
        } catch (Exception e) {
            System.out.println("Internal Resources could not be found || e: " + e.getMessage());
        }
    }

    private void getBindingsFromInternalResources() throws Exception {
        for (int i = 0; i <= MAX_INTERNAL_BINDINGS; i++) {
            URI uri = this
                    .getClass()
                    .getClassLoader()
                    .getResource("bindings/shadow/" + i + ".png")
                    .toURI();
            bindings.add(new Binding(uri));
        }

        HashMap<Binding, Integer> bindingIntensityMap = new HashMap<>();

        for (int j = 0; j < this.bindings.size(); j++) {
            convertToRBGIntImages(bindings.get(j));
            setIntensityFromBindings(bindingIntensityMap, bindings.get(j));
        }

        optimizedBindings = new ArrayList<>(getSortedIntensity(bindingIntensityMap)).toArray(new Binding[bindings.size()]);
    }

    private Binding getCustomBinding(String path) throws Exception {
        URI uri = this
                .getClass()
                .getClassLoader()
                .getResource(path)
                .toURI();
        return new Binding(uri);
    }

    public Binding[] getOptimizedBindings() {
        return optimizedBindings;
    }

    public List<Binding> getSortedBindings() {
        HashMap<Binding, Integer> bindingIntensityMap = new HashMap<>();

        for (int j = 0; j < this.bindings.size(); j++) {
            convertToRBGIntImages(bindings.get(j));
            setIntensityFromBindings(bindingIntensityMap, bindings.get(j));
        }

        return new ArrayList<>(getSortedIntensity(bindingIntensityMap));
    }

    //TODO change?
    private Set<Binding> getSortedIntensity(HashMap<Binding, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new)).keySet();
    }

    private void convertToRBGIntImages(Binding binding) {
        binding.setBindingImage(ImageUtil.convertImageToRGBInt(binding.getBindingImage()));
    }
    private void setIntensityFromBindings(HashMap map, Binding binding) {
        map.put(binding, binding.getIntensityCount());
    }

}
