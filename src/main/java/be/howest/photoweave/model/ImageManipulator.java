package be.howest.photoweave.model;

import be.howest.photoweave.model.imaging.FilteredImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageManipulator {
    private BufferedImage originalImage;
    private FilteredImage filteredImage;

    private Integer posterizeScale = 10;

    public ImageManipulator(String path) throws IOException {
        originalImage = ImageIO.read(new File(path));
        filteredImage = new FilteredImage(originalImage);
    }
}
