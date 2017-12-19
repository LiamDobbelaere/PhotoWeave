package be.howest.photoweave.model.binding;

import be.howest.photoweave.model.util.ImageUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class BindingFactory {

    private List<Binding> bindings = new ArrayList<>();
    private final Integer MAX_INTERNAL_BINDINGS = 24;
    private Binding[] optimizedBindings;
    private HashMap<String, List<Binding>> allBindings = new HashMap<>();

    public BindingFactory() {
        try {
            getBindingsFromInternalResources();
            System.out.println("ok");
        } catch (Exception e) {
            System.out.println("Internal Resources could not be found || e: " + e.getMessage());
        }
    }

    private void getBindingsFromInternalResources() throws Exception {
        File[] directories = new File("./bindings").listFiles(File::isDirectory);

        for (File directory : directories){
            Collection<File> files = FileUtils.listFiles(
                    directory, new String[] {"png"}, true);

            System.out.println(directory.getAbsolutePath().toUpperCase());
            List<Binding> localBindings = new ArrayList<>();
            for (File file : files) {
                System.out.println(file.getPath());
                InputStream is = new FileInputStream(file);
                Binding b = new Binding(is,file.getName());
                localBindings.add(b);
                this.bindings.add(b);
            }
            allBindings.put(directory.getName(),localBindings);
        }

        HashMap<Binding, Integer> bindingIntensityMap = new HashMap<>();

        for (int j = 0; j < this.bindings.size(); j++) {
            convertToRBGIntImages(this.bindings.get(j));
            setIntensityFromBindings(bindingIntensityMap, bindings.get(j));
        }

        optimizedBindings = new ArrayList<>(getSortedIntensity(bindingIntensityMap)).toArray(new Binding[bindings.size()]);
        System.out.println(optimizedBindings);
    }

    private Binding getCustomBinding(String path) throws Exception {
        URI uri = this
                .getClass()
                .getClassLoader()
                .getResource(path)
                .toURI();
        return null;
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

    public HashMap<String, List<Binding>> getAllBindings() {
        return allBindings;
    }
}