package be.howest.photoweave.model.binding;

import be.howest.photoweave.model.util.ImageUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BindingFactory {
    private List<Binding> bindings = new ArrayList<>();
    private final Integer MAX_INTERNAL_BINDINGS = 24;
    private Binding[] optimizedBindings;

    /**
     * Responsible for loading the bindings and keeping an array of all the bindings
     */
    public BindingFactory() {
        try {
            getBindingsFromInternalResources();
        } catch (Exception e) {
            System.out.println("Internal Resources could not be found || e: " + e.getMessage());
        }
    }

    private void getBindingsFromInternalResources() throws Exception {
        Collection<File> files = FileUtils.listFiles(
                new File(Paths.get("./bindings").toAbsolutePath().normalize().toString()), new String[]{"png"}, true);

        for (File file : files) {
            InputStream is = new FileInputStream(file);
            bindings.add(new Binding(is));
        }

        HashMap<Binding, Integer> bindingIntensityMap = new HashMap<>();

        for (int j = 0; j < this.bindings.size(); j++) {
            convertToRBGIntImages(bindings.get(j));
            setIntensityFromBindings(bindingIntensityMap, bindings.get(j));
        }

        optimizedBindings = new ArrayList<>(getSortedIntensity(bindingIntensityMap)).toArray(new Binding[bindings.size()]);
    }

    public Binding[] getOptimizedBindings() {
        return optimizedBindings;
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
