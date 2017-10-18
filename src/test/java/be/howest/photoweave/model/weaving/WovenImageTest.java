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
    private BufferedImage shade0;
    private BufferedImage shade1;
    private BufferedImage shade2;
    private BufferedImage shade3;

    private BufferedImage[] shadowShades = new BufferedImage[25];

    @Before
    public void setUp() throws Exception {
        this.polar4Levels = ImageIO.read(new File(this.getClass().getClassLoader().getResource("polar_4levels.png").toURI()));
        this.polar24Levels = ImageIO.read(new File(this.getClass().getClassLoader().getResource("polar_24levels.png").toURI()));
        this.shade0 = ImageIO.read(new File(this.getClass().getClassLoader().getResource("shade0.bmp").toURI()));
        this.shade1 = ImageIO.read(new File(this.getClass().getClassLoader().getResource("shade1.bmp").toURI()));
        this.shade2 = ImageIO.read(new File(this.getClass().getClassLoader().getResource("shade2.bmp").toURI()));
        this.shade3 = ImageIO.read(new File(this.getClass().getClassLoader().getResource("shade3.bmp").toURI()));

        for (int i = 0; i <= 24; i++) {
            shadowShades[i] = ImageIO.read(new File(this.getClass().getClassLoader().getResource("shadow/" + i + ".png").toURI()));
        }
    }

    @Test
    public void testOutputMustManuallyCheck() throws Exception {
        WovenImage wovenImage = new WovenImage(this.polar4Levels);

        wovenImage.setPattern(0, this.shade3);
        wovenImage.setPattern(1, this.shade2);
        wovenImage.setPattern(2, this.shade1);
        wovenImage.setPattern(3, this.shade0);

        wovenImage.redraw();

        ImageIO.write(wovenImage.getResultImage(), "png", new File("test.png"));
    }

    @Test
    public void testOutputShadowMustManuallyCheck() throws Exception {
        WovenImage wovenImage = new WovenImage(this.polar24Levels);

        for (int i = 0; i < 24; i++) {
            wovenImage.setPattern(i, shadowShades[i]);
        }

        wovenImage.redraw();

        ImageIO.write(wovenImage.getResultImage(), "png", new File("test_shadow.png"));

    }

}