package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.ParametersInterface;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.customFile.data.CustomFile;
import be.howest.photoweave.model.customFile.data.Mutation;
import be.howest.photoweave.model.customFile.data.UserInterfaceData;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.imagefilters.FloatersFilter;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.GrayscaleFilter;
import be.howest.photoweave.model.imaging.rgbfilters.PosterizeFilter;
import com.google.gson.JsonObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LoadFilteredImageController {

    private UserInterfaceData userInterfaceData;
    private JsonObject jsonObject;

    public FilteredImage getFilteredImage() {
        return filteredImage;
    }

    private FilteredImage filteredImage;

    private ParametersInterface parent;

    public LoadFilteredImageController(BufferedImage image, int posterization, boolean enableFloaters, int xFloater, int yFloater,  ParametersInterface parent){
        load(image,posterization,enableFloaters,xFloater,yFloater,false,null,parent);
    }

    public LoadFilteredImageController(File jsonFile, ParametersInterface parent) throws IOException {
        RawDataDecoder decoder = new RawDataDecoder(jsonFile);
        decoder.decode();

        CustomFile file = decoder.getCustomFile();
        Mutation mutation = file.getMutation();
        UserInterfaceData userInterfaceData = file.getUserInterface();
        this.userInterfaceData = userInterfaceData;

        load(decoder.getImage(),mutation.getPosterization(),false,userInterfaceData.getxFloater(),userInterfaceData.getyFloater(),userInterfaceData.isInverted(),decoder.getBindingMap(),parent);
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
        this.parent.setUIComponentInverted(this.userInterfaceData.isInverted()); //DURING INIT
        //this.parent.setUICompentenPosterize(); // poseterization before hook //
        this.parent.setUIComponentHeight(this.userInterfaceData.getHeight()); //DURING INIT
        this.parent.setUIComponentWidth(this.userInterfaceData.getWidth()); //DURING INIT
        this.parent.setUIComponentMarked(this.userInterfaceData.isMarked()); //
        this.parent.setUIComponentXScroll(this.userInterfaceData.getxScroll());
        this.parent.setUIComponentYScroll(this.userInterfaceData.getyScroll());
    }
}
