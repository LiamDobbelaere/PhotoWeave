package be.howest.photoweave.model.marvin.image;

import be.howest.photoweave.model.marvin.weave.PATTERN;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageCorrection {

    private Color[][] pixelMatrix;

    private Integer COLOR_WHITE = -1;
    private Integer COLOR_BLACK = -16777216;
    private Integer COLOR_ORANGE = -32985;
    private Integer COLOR_BLUE = -16755216;

    public ImageCorrection(File file) {
        try {
            pixelMatrix = loadPixelsFromImage(ImageIO.read(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* VV Duplicate | Refactor VV */
    public void fillCorrection(String pathOutput) throws IOException {
        //Correcting white spaces
        BufferedImage tempImage = fillEmptySpace(pixelMatrix, COLOR_WHITE, COLOR_BLACK);

        //Correcting black spaces
        Color[][] colors2 = loadPixelsFromImage(tempImage);
        BufferedImage finalImage = fillEmptySpace(colors2, COLOR_BLACK, COLOR_WHITE);

        File outputfile = new File(pathOutput);
        ImageIO.write(finalImage, "bmp", outputfile);
    }
    public void patternCorrection(String pathOutput) throws IOException {
        //Correcting white spaces
        BufferedImage tempImage = patternEmptySpace(pixelMatrix, COLOR_WHITE, PATTERN.FOUR);

        //Correcting black spaces
        Color[][] colors2 = loadPixelsFromImage(tempImage);
        BufferedImage finalImage = patternEmptySpace(colors2, COLOR_BLACK, PATTERN.ONE);

        File outputfile = new File(pathOutput);
        ImageIO.write(finalImage, "bmp", outputfile);
    }

    private Color[][] loadPixelsFromImage(BufferedImage image) {
        Color[][] colors = new Color[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                colors[x][y] = new Color(image.getRGB(x, y));
            }
        }

        return colors;
    }

    private BufferedImage fillEmptySpace(Color[][] pixelMatrix, Integer FIRST_COLOR, Integer SECOND_COLOR) {
        BufferedImage image = new BufferedImage(pixelMatrix.length, pixelMatrix[0].length, BufferedImage.TYPE_INT_RGB);
        Integer trailingPixels = 0;
        Boolean isContinuous = false;

        //TODO maak leesbaar
        for (int j = 0; j < pixelMatrix[0].length; j++) {
            for (int i = 0; i < pixelMatrix.length; i++) {
                if (pixelMatrix[i][j].getRGB() == FIRST_COLOR) {
                    if (isContinuous) trailingPixels++;
                    if (trailingPixels >= 4) {
                        image.setRGB(i, j, SECOND_COLOR);
                        trailingPixels = 0;
                    } else {
                        image.setRGB(i, j, pixelMatrix[i][j].getRGB());
                    }
                    isContinuous = true;
                } else {
                    image.setRGB(i, j, pixelMatrix[i][j].getRGB());
                    isContinuous = false;
                    trailingPixels = 0;
                }
            }
        }
        return image;
    }
    private BufferedImage patternEmptySpace(Color[][] pixelMatrix, Integer FIRST_COLOR, Integer[][] PATTERN) {
        BufferedImage image = new BufferedImage(pixelMatrix.length, pixelMatrix[0].length, BufferedImage.TYPE_INT_RGB);

        for (int j = 0; j < pixelMatrix[0].length; j++) {
            for (int i = 0; i < pixelMatrix.length; i++) {
                if (pixelMatrix[i][j].getRGB() == FIRST_COLOR) {
                    Integer temp_i = i % 10;
                    Integer temp_j = j % 10;
                    image.setRGB(i, j, PATTERN[temp_j][temp_i]);
                }
            }
        }
        return image;
    }
}
