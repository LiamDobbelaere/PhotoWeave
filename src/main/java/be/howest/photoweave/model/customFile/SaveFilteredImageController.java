package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.customFile.data.CustomFile;
import be.howest.photoweave.model.customFile.data.UserInterfaceData;
import be.howest.photoweave.model.imaging.FilteredImage;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;

public class SaveFilteredImageController {
    private FilteredImage filteredImage;
    private UserInterfaceData userInterfaceData;

    public SaveFilteredImageController(FilteredImage filteredImage,UserInterfaceData userInterfaceData) {
        this.filteredImage = filteredImage;
        this.userInterfaceData = userInterfaceData;
    }

    public void save(String path) throws IOException {
        Gson g = new Gson();
        RawDataEncoder encoder = new RawDataEncoder(this.filteredImage, this.userInterfaceData);

        encoder.encode();
        CustomFile fileData = encoder.getRawData();

        String json = g.toJson(fileData);
        System.out.println(json);
        try(  PrintWriter out = new PrintWriter( path )  ){
            out.println( json );
        }
    }
}
