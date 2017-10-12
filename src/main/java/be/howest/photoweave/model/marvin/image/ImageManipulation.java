package be.howest.photoweave.model.marvin.image;


import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

import static marvin.MarvinPluginCollection.halftoneErrorDiffusion;

public class ImageManipulation {

    public static void saveAsHalftoneImage(String pathOriginal, String pathOutput){
        MarvinImage original = MarvinImageIO.loadImage(pathOriginal);
        MarvinImage output = original.clone();
        halftoneErrorDiffusion(original, output);
        MarvinImageIO.saveImage(output,pathOutput);
    }

}
