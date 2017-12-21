package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.ParametersInterface;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.imagefilters.FloatersFilter;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.GrayscaleFilter;
import be.howest.photoweave.model.imaging.rgbfilters.PosterizeFilter;
import be.howest.photoweave.model.util.ImageUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LoadFilteredImageController {

    private JsonObject jsonObject;

    public FilteredImage getFilteredImage() {
        return filteredImage;
    }

    private FilteredImage filteredImage;

    private ParametersInterface parent;

    public LoadFilteredImageController(BufferedImage image, int posterization, boolean enableFloaters, int xFloater, int yFloater,  ParametersInterface parent){
        load(image,posterization,enableFloaters,xFloater,yFloater,false,null,parent);
    }

    public LoadFilteredImageController(File customFile, ParametersInterface parent) throws IOException {
        RawDataDecoder decoder = new RawDataDecoder(customFile);
        decoder.decode();
        load(image,posterization,false,0,3,inverted,bindingMap,parent);
    }

    private void load(BufferedImage image, int posterization, boolean enableFloaters, int xFloater, int yFloater,boolean inverted, Map<Integer,Binding> bindingMap,  ParametersInterface parent){
        this.parent = parent;

        this.filteredImage = new FilteredImage(image);
        this.filteredImage.addThreadEventListener(parent);
        this.filteredImage.getFilters().add(new GrayscaleFilter());
        this.filteredImage.getFilters().add(new PosterizeFilter());
        this.filteredImage.getFilters().add(new BindingFilter(
                (PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class), filteredImage));

        BindingFilter bf = ((BindingFilter)this.filteredImage.getFilters().findRGBFilter(BindingFilter.class));
        Map<Integer, Binding> bm = bf.getBindingsMap();
        if (bindingMap != null) bm.putAll(bindingMap);

        this.filteredImage.getFilters().add(new FloatersFilter(enableFloaters));

        ((PosterizeFilter) this.filteredImage.getFilters().findRGBFilter(PosterizeFilter.class))
                .setLevels(posterization);

        FloatersFilter floatersFilter = (FloatersFilter) this.filteredImage.getFilters().findImageFilter(FloatersFilter.class);
        floatersFilter.setFloaterTresholdX(xFloater);
        floatersFilter.setFloaterTresholdY(yFloater);
    }

    public void loadDataInUserInterface(){
        this.parent.setUIComponentInverted(this.jsonObject
                .get("image")
                .getAsJsonObject()
                .get("inverted")
                .getAsBoolean());
        /*this.parent.setUIComponentMarked(this.jsonObject
                .get("image")
                .getAsJsonObject()
                .get("marked")
                .getAsBoolean());

        this.parent.setUICompentenPosterize(this.jsonObject
                .get("mutation")
                .getAsJsonObject()
                .get("posterization")
                .getAsInt());


        this.parent.setUIComponentHeight(this.jsonObject
                .get("mutation")
                .getAsJsonObject()
                .get("scale")
                .getAsJsonObject()
                .get("height")
                .getAsInt());

        this.parent.setUIComponentWidth(this.jsonObject
                .get("mutation")
                .getAsJsonObject()
                .get("scale")
                .getAsJsonObject()
                .get("width")
                .getAsInt());
        */

    }
}
