package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.customFile.data.*;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.PosterizeFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilteredImageToRawDataConverter {
    private FilteredImage filteredImage;
    private CustomFile rawData;

    public FilteredImageToRawDataConverter(FilteredImage filteredImage) {
        this.filteredImage = filteredImage;
    }

    public void convert() throws IOException {
        /* Image */
        BufferedImage image = this.filteredImage.getOriginalImage();
        BufferedImage modImage = this.filteredImage.getModifiedImage();
        ImageData imageData = new ImageData(ImageUtil.convertImageToBase64(ImageUtil.convertImageToRGBInt(image)),image.getWidth(),image.getHeight());
        /* ----- */

        /* Bindings */
        BindingFilter bf = ((BindingFilter)this.filteredImage.getFilters().findRGBFilter(BindingFilter.class));
        List<BindingData> bindings = new ArrayList<BindingData>();
        Map<Integer, Binding> bm = bf.getBindingsMap();

        List<Region> sl = bf.getRegions();


        bm.forEach((index, binding) -> {
            try {
                bindings.add(new BindingData(binding.getName(), index, ImageUtil.convertImageToBase64(binding.getBindingImage())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        /* ------- */


        Mutation mutation = new Mutation(new Scale(modImage.getWidth(),modImage.getHeight()),((PosterizeFilter) this.filteredImage.getFilters().findRGBFilter(PosterizeFilter.class)).getLevelCount(),bindings);
        UserInterface userInterface = null;

        this.rawData = new CustomFile(imageData,mutation,userInterface);
    }

    public FilteredImage getFilteredImage() {
        return filteredImage;
    }

    public CustomFile getRawData() {
        return rawData;
    }
}
