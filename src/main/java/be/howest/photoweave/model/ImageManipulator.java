package be.howest.photoweave.model;

import be.howest.photoweave.model.imaging.MonochromeImage;
import be.howest.photoweave.model.weaving.WovenImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageManipulator {
    private BufferedImage originalImage;
    private MonochromeImage monochromeImage;
    private WovenImage wovenImage;

    private Integer posterizeScale = 10;

    public ImageManipulator(String path) throws IOException {
        originalImage = ImageIO.read(new File(path));
        monochromeImage = new MonochromeImage(originalImage);
    }
}
