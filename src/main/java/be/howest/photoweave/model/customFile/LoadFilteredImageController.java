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
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoadFilteredImageController {

    private List<Region> regions;
    private UserInterfaceData userInterfaceData;

    public FilteredImage getFilteredImage() {
        return filteredImage;
    }

    private FilteredImage filteredImage;

    private ParametersInterface parent;

    public List<Region> getRegions() {
        return regions;
    }

    public LoadFilteredImageController(BufferedImage image, int posterization, boolean enableFloaters, int xFloater, int yFloater, ParametersInterface parent){
        load(image,posterization,enableFloaters,xFloater,yFloater,-1, -1,false,null,null,parent);
    }

    public LoadFilteredImageController(File jsonFile, ParametersInterface parent) throws IOException {
        RawDataDecoder decoder = new RawDataDecoder(jsonFile);
        decoder.decode();

        CustomFile file = decoder.getCustomFile();
        Mutation mutation = file.getMutation();
        UserInterfaceData userInterfaceData = file.getUserInterface();
        this.userInterfaceData = userInterfaceData;

        System.out.println("LOAD JSON" + decoder.getRegions());

        load(decoder.getImage(),mutation.getPosterization(),false,userInterfaceData.getxFloater(),userInterfaceData.getyFloater(),mutation.getWidth(), mutation.getHeight(),userInterfaceData.isInverted(),decoder.getBindingMap(), decoder.getRegions(), parent);
    }

    private void load(BufferedImage image, int posterization, boolean enableFloaters, int xFloater, int yFloater,int width, int height, boolean inverted, Map<Integer,Binding> bindingMap, List<Region> regions,  ParametersInterface parent){
        /* EditPhoto Controller */
        this.parent = parent;

        /* FilteredImage Filters */
        this.filteredImage = new FilteredImage(image);
        this.filteredImage.addThreadEventListener(parent);
        this.filteredImage.getFilters().add(new GrayscaleFilter());
        this.filteredImage.getFilters().add(new PosterizeFilter());
        this.filteredImage.getFilters().add(new BindingFilter((PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class), filteredImage));
        this.filteredImage.getFilters().add(new FloatersFilter(enableFloaters));

        /* Inserting some data before hooking listeners */
        if (regions != null) loadDataBeforeListenAreHooked(posterization,width,height);

        /* Inserting Custom Bindings */
        BindingFilter bf = ((BindingFilter)this.filteredImage.getFilters().findRGBFilter(BindingFilter.class));
        Map<Integer, Binding> bm = bf.getBindingsMap();
        if (bindingMap != null) bm.putAll(bindingMap);

        /* Inserting Custom Regions */
        List<Region> rl = bf.getRegions();
        if (regions != null) rl.addAll(regions);this.regions = regions;

        /* Inserting Custom Posterization */
        ((PosterizeFilter) this.filteredImage.getFilters().findRGBFilter(PosterizeFilter.class)).setLevels(posterization);

        /* Inserting Custom Floaters data */
        FloatersFilter floatersFilter = (FloatersFilter) this.filteredImage.getFilters().findImageFilter(FloatersFilter.class);
        floatersFilter.setFloaterTresholdX(xFloater);
        floatersFilter.setFloaterTresholdY(yFloater);
    }

    /* WHAT WILL CAUSE A FILTEREDIMAGE RESET
    * ! Changing Posterize
    * ! Changing FilterImage scale (not PixelatedImageView scale)
    * */

    /* SETTING UI PARAMETERS FROM CUSTOM FILE
    * Constructor() -> UI nodes not loaded
    * initialize() -> UI nodes being hooked to controller
    * [During] initialize() -> Listeners are hooked to UI nodes
    * [After] "Hooked Listeners" in initialize -> loadDataInUserInterface <-
    * [After] initialize() -> Logic is applied
    * */


    /* WHEN IT (ACTUALLY) IS APPLIED
    * Constructor() -> UI nodes not loaded
    * initialize() -> UI nodes being hooked to controller
    * -> posterization
    * -> filterImage scale (width, height)
    * [During] initialize() -> Listeners are hooked to UI nodes
    * -> UI: inverted
    * -> UI: PixelatedImageView width, height
    * -> UI: Floater x,y
    * [After] initialize() -> Logic is applied
    * -> UI: scrollX, scrollY
    * */
    public void loadDataInUserInterface(){
        this.parent.setUIComponentInverted(this.userInterfaceData.isInverted());
        this.parent.setUIComponentViewHeight(this.userInterfaceData.getViewHeight());
        this.parent.setUIComponentViewWidth(this.userInterfaceData.getViewWidth());
        this.parent.setUIComponentMarked(this.userInterfaceData.isMarked());
        this.parent.setUIComponentXFloater(this.userInterfaceData.getxFloater());
        this.parent.setUIComponentYFloater(this.userInterfaceData.getyFloater());
        this.parent.setUIComponentXScroll(this.userInterfaceData.getxScroll());
        this.parent.setUIComponentYScroll(this.userInterfaceData.getyScroll());

    }

    public void loadDataBeforeListenAreHooked(int posterization, int width, int height){
        this.filteredImage.resize(width, height);
        this.parent.setUIComponentWidth(width);
        this.parent.setUIComponentHeight(height);

        ((PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class)).setLevels(posterization);
        this.parent.setUICompentenPosterize(posterization);

    }
}

