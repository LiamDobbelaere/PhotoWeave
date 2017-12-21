package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.customFile.data.CustomFile;
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

public class RawDataDecoder {
    private String json;
    private Gson g;
    private JsonObject jsonObject;
    private String base64;
    private int width;
    private int height;
    private int posterization;
    private JsonArray bindingArray;
    private boolean inverted;
    private BufferedImage image;
    
    

    private Map<Integer, Binding> bindingMap;
    private CustomFile customFile;


    public RawDataDecoder(File file) throws IOException {
        this.json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        this.g = new Gson();
        //JsonObject jsonObject = g.fromJson(json, JsonObject.class);
        //this.jsonObject = jsonObject;
    }

    public void decode() throws IOException {
/*        this.base64 = jsonObject
                .get("image")
                .getAsJsonObject()
                .get("base64")
                .getAsString();

        this.width = jsonObject
                .get("image")
                .getAsJsonObject()
                .get("width")
                .getAsInt();

        this.height = jsonObject
                .get("image")
                .getAsJsonObject()
                .get("height")
                .getAsInt();

        this.posterization = jsonObject
                .get("mutation")
                .getAsJsonObject()
                .get("posterization")
                .getAsInt();

        this.bindingArray = jsonObject
                .get("mutation")
                .getAsJsonObject()
                .get("bindingpalette")
                .getAsJsonArray();

        this.inverted = jsonObject
                .get("image")
                .getAsJsonObject()
                .get("inverted")
                .getAsBoolean();



*/

        this.customFile = g.fromJson(json, CustomFile.class);

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

        /*
         * Alt
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b64ImageBytes = decoder.decodeBuffer(base64);
         */

        //ArrayIndexOutOfBoundsException; image is to big
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
}
