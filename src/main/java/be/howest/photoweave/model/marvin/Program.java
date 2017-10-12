package be.howest.photoweave.model.marvin;

import be.howest.photoweave.model.marvin.image.ImageCorrection;
import be.howest.photoweave.model.marvin.image.ImageManipulation;

import java.io.File;
import java.io.IOException;

public class Program {
    public Program(){

    }

    public static void main(String[] args) throws IOException {
        String ORIGINAL_PATH =".\\images\\original\\bear.jpg";
        String HALFTONE_PATH =".\\images\\results\\halftone\\bear_new_halftone.png";
        String RESULT_SIMPLE_CORRECTION_PATH = ".\\images\\results\\simple\\bear_simple.bmp";
        String RESULT_PATTERN_CORRECTION_PATH = ".\\images\\results\\pattern\\bear_pattern.bmp";

        ImageManipulation.saveAsHalftoneImage(ORIGINAL_PATH,HALFTONE_PATH);
        ImageCorrection imageCorrection = new ImageCorrection(new File(HALFTONE_PATH));
        imageCorrection.fillCorrection(RESULT_SIMPLE_CORRECTION_PATH);
        imageCorrection.patternCorrection(RESULT_PATTERN_CORRECTION_PATH);
    }
}
