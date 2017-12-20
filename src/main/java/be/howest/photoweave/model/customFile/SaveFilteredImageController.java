package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.customFile.data.CustomFile;
import be.howest.photoweave.model.imaging.FilteredImage;
import com.google.gson.Gson;

import java.io.IOException;

public class SaveFilteredImageController {
    private FilteredImage filteredImage;

    public SaveFilteredImageController(FilteredImage filteredImage) {
        this.filteredImage = filteredImage;
    }

    public void save(String path) throws IOException {
        Gson g = new Gson();
        /*JsonObject innerObject = new JsonObject();
        innerObject.addProperty("image", "john");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("publisher", innerObject);


        BindingFilter bf = ((BindingFilter)this.filteredImage.getFilters().findRGBFilter(BindingFilter.class));
        Map<Integer, Binding> bm = bf.getBindingsMap();
        String json = g.toJson(new Scale(10,5587));
        System.out.println(json);
        System.out.println(bm);*/


        FilteredImageToRawDataConverter converter = new FilteredImageToRawDataConverter(this.filteredImage);

        converter.convert();
        CustomFile fileData = converter.getRawData();

        String json = g.toJson(fileData);
        System.out.println(json);
        System.out.println("");
    }
}
