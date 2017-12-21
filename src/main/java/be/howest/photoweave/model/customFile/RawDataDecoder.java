package be.howest.photoweave.model.customFile;

import be.howest.photoweave.model.binding.Binding;
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
    private Gson g;
    private JsonObject jsonObject;
    private String base64;
    private int width;
    private int height;
    private int posterization;
    private JsonArray bindingArray;
    private boolean inverted;
    private BufferedImage image;


    public RawDataDecoder(File customFile) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(customFile.getAbsolutePath())));
        this.g = new Gson();
        JsonObject jsonObject = g.fromJson(json, JsonObject.class);
        this.jsonObject = jsonObject;
    }

    public void decode() throws IOException {
        this.base64 = jsonObject
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

        Map<Integer,Binding> bindingMap = new HashMap<>();
        this.bindingArray.forEach(jsonElement -> {
            String bName = jsonElement.getAsJsonObject().get("name").getAsString();
            Integer bIndex = jsonElement.getAsJsonObject().get("index").getAsInt();
            String bBase64 = jsonElement.getAsJsonObject().get("base64").getAsString();

            try {
                bindingMap.put(bIndex,new Binding(bBase64,bName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        //BASE64Decoder decoder = new BASE64Decoder();
        //byte[] b64ImageBytes = decoder.decodeBuffer(base64);

        //ArrayIndexOutOfBoundsException; image is to big
        byte[] b64ImageBytes = DatatypeConverter.parseBase64Binary(base64);

        this.image = ImageUtil.convertImageToRGBInt(ImageIO.read(new ByteArrayInputStream(b64ImageBytes)));
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public String getBase64() {
        return base64;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPosterization() {
        return posterization;
    }

    public JsonArray getBindingArray() {
        return bindingArray;
    }

    public boolean isInverted() {
        return inverted;
    }

    public BufferedImage getImage() {
        return image;
    }
}
