package be.howest.photoweave.model.imaging;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;

import java.io.File;

import static org.junit.Assert.*;

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
    public void testGrayscale16ToMonochromeHasBlackLeftWhiteRight() throws Exception {
        MonochromeImage mi = new MonochromeImage(ImageIO.read(grayscale16));
        mi.redraw();
        ImageIO.write(mi.getModifiedImage(), "png", new File("test.png"));

        assertEquals("Far left pixel of 2-level posterized grayscale16 should be black",
                0xff000000, mi.getModifiedImage().getRGB(0, 0));
        assertEquals("Far right pixel of 2-level posterized grayscale16 should be white",
                0xffffffff, mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() - 1, 0));
    }
}