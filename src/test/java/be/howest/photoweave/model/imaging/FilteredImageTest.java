package be.howest.photoweave.model.imaging;

import net.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by tomdo on 6/10/2017.
 */
public class FilteredImageTest {
    private File grayscale16;

    @Before
    public void setUp() throws Exception {
        this.grayscale16 = new File(this.getClass().getClassLoader().getResource("grayscale16.png").toURI());
        System.out.println("Created lock!");
    }

    @Test
    public void testGrayscale16ToMonochrome2LevelsHasBlackLeftWhiteRight() throws Exception {
        final Waiter waiter = new Waiter();

        FilteredImage mi = new FilteredImage(ImageIO.read(grayscale16));
        mi.addThreadEventListener(new ThreadEventListener() {
            @Override
            public void onThreadComplete() {

            }

            @Override
            public void onRedrawComplete() {
                waiter.resume();
            }
        });
        mi.redraw();

        waiter.await(5000);

        assertEquals("Far left pixel of 2-level posterized grayscale16 should be black",
                Color.black.getRGB(), mi.getModifiedImage().getRGB(0, 0));
        assertEquals("Far right pixel of 2-level posterized grayscale16 should be white",
                Color.white.getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() - 1, 0));
    }

    @Test
    public void testGrayscale16ToMonochrome3LevelsHasGrayCenter() throws Exception {
        final Waiter waiter = new Waiter();

        FilteredImage mi = new FilteredImage(ImageIO.read(grayscale16));
        mi.setLevels(3);

        mi.addThreadEventListener(new ThreadEventListener() {
            @Override
            public void onThreadComplete() {

            }

            @Override
            public void onRedrawComplete() {
                waiter.resume();
            }
        });
        mi.redraw();

        waiter.await(5000);

        assertEquals("Center pixel of 3-level posterized grayscale16 should be 127, 127, 127",
                new Color(127, 127, 127).getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() / 2, 0));
    }

    @Test
    public void testGrayscale16ToMonochrome4LevelsHasGrayCenter() throws Exception {
        final Waiter waiter = new Waiter();

        FilteredImage mi = new FilteredImage(ImageIO.read(grayscale16));
        mi.setLevels(4);

        mi.addThreadEventListener(new ThreadEventListener() {
            @Override
            public void onThreadComplete() {

            }

            @Override
            public void onRedrawComplete() {
                waiter.resume();
            }
        });
        mi.redraw();

        waiter.await(5000);

        assertEquals("2nd block of 4-level posterized grayscale16 should be 85, 85, 85",
                new Color(85, 85, 85).getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() / 4, 0));

        assertEquals("3rd block of 4-level posterized grayscale16 should be 170, 170, 170",
                new Color(170, 170, 170).getRGB(), mi.getModifiedImage().getRGB(mi.getModifiedImage().getWidth() / 4 * 2, 0));
    }

}