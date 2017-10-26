package be.howest.photoweave.model.weaving;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by tomdo on 6/10/2017.
 */
public class WovenImageTest {
    private BufferedImage polar4Levels;
    private BufferedImage polar24Levels;

    @Before
    public void setUp() throws Exception {
        this.polar4Levels = ImageIO.read(new File(this.getClass().getClassLoader().getResource("polar_4levels.png").toURI()));
        this.polar24Levels = ImageIO.read(new File(this.getClass().getClassLoader().getResource("polar_24levels.png").toURI()));
    }

    @Test
    public void testOutputMustManuallyCheck() throws Exception {
        WovenImage wovenImage = new WovenImage(this.polar4Levels);
        wovenImage.redraw();

        ImageIO.write(wovenImage.getResultImage(), "png", new File("test.png"));
    }

    @Test
    public void testOutputShadowMustManuallyCheck() throws Exception {
        WovenImage wovenImage = new WovenImage(this.polar24Levels);
        wovenImage.redraw();

        ImageIO.write(wovenImage.getResultImage(), "png", new File("test_shadow.png"));

    }

}