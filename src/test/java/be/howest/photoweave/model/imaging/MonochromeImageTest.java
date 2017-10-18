package be.howest.photoweave.model.imaging;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

import static org.junit.Assert.*;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by tomdo on 6/10/2017.
 */
public class MonochromeImageTest {
    private File grayscale16;

    @Before
    public void setUp() throws Exception {
        this.grayscale16 = new File(this.getClass().getClassLoader().getResource("grayscale16.png").toURI());
    }

    @Test
    public void testGrayscale16ToMonochrome2LevelsHasBlackLeftWhiteRight() throws Exception {
        MonochromeImage mi = new MonochromeImage(ImageIO.read(grayscale16));
        mi.redraw();

        assertEquals("Far left pixel of 2-level posterized grayscale16 should be black",
                Color.black.getRGB(), mi.getModifiedImage().getRGB(0, 0));
        assertEquals("Far right pixel of 2-level posterized grayscale16 should be white",
                Color.white.getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() - 1, 0));
    }

    @Test
    public void testGrayscale16ToMonochrome3LevelsHasGrayCenter() throws Exception {
        MonochromeImage mi = new MonochromeImage(ImageIO.read(grayscale16));
        mi.setLevels(3);
        mi.redraw();

        assertEquals("Center pixel of 3-level posterized grayscale16 should be 127, 127, 127",
                new Color(127, 127, 127).getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() / 2, 0));
    }

    @Test
    public void testGrayscale16ToMonochrome4LevelsHasGrayCenter() throws Exception {
        MonochromeImage mi = new MonochromeImage(ImageIO.read(grayscale16));
        mi.setLevels(4);
        mi.redraw();

        assertEquals("2nd block of 4-level posterized grayscale16 should be 85, 85, 85",
                new Color(85, 85, 85).getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() / 4, 0));

        assertEquals("3rd block of 4-level posterized grayscale16 should be 170, 170, 170",
                new Color(170, 170, 170).getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() / 4 * 2, 0));
    }

}