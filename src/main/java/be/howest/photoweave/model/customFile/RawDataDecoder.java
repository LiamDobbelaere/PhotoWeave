package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.customFile.data.CustomFile;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.ImageUtil;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawDataDecoder {
    private String json;
    private Gson g;
    private BufferedImage image;

    private Map<Integer, Binding> bindingMap;
    private CustomFile customFile;
    private List<Region> regions;


    public RawDataDecoder(File file) throws IOException {
        this.json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        this.g = new Gson();
    }

    public void decode() throws IOException {
        this.customFile = g.fromJson(json, CustomFile.class);

        /* Bindings */
        this.bindingMap = new HashMap<>();
        this.customFile.getMutation().getBindingpalette().forEach(bindingData -> {
            String bName = bindingData.getName();
            Integer bIndex = bindingData.getIndex();
            String bBase64 = bindingData.getBase64();

            try {
                bindingMap.put(bIndex,new Binding(bBase64,bName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        /* ----- */

        /* Regions */
        this.regions = new ArrayList<>();
        this.customFile.getMutation().getRegionData().forEach(regionData -> {
            String bName = regionData.getBindingData().getName();
            Integer bIndex = regionData.getBindingData().getIndex();
            String bBase64 = regionData.getBindingData().getBase64();
            
            List<Point> selection = regionData.getPoints();
            Region newRegion = new Region(selection);

            Binding newBinding = null;
            try {
                newBinding = new Binding(bBase64,bName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            newRegion.setTargetLevel(bIndex);
            newRegion.setTargetBinding(newBinding);

            regions.add(newRegion);
        });

        //ALTERNATIVE -> BASE64Decoder decoder = new BASE64Decoder(); byte[] b64ImageBytes = decoder.decodeBuffer(base64);
        byte[] b64ImageBytes = DatatypeConverter.parseBase64Binary(this.customFile.getImage().getBase64());
        this.image = ImageUtil.convertImageToRGBInt(ImageIO.read(new ByteArrayInputStream(b64ImageBytes)));
    }

    public BufferedImage getImage() {
        return image;
    }

    public Map<Integer, Binding> getBindingMap() {
        return bindingMap;
    }

    public CustomFile getCustomFile() {
        return customFile;
    }

    public List<Region> getRegions() {
        return regions;
    }
}
