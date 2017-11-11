package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.imaging.filters.GrayscaleFilter;
import be.howest.photoweave.model.imaging.filters.PosterizeFilter;
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
    private FilteredImage filteredImage;

    @Before
    public void setUp() throws Exception {
        this.grayscale16 = new File(this.getClass().getClassLoader().getResource("grayscale16.png").toURI());
        this.filteredImage = new FilteredImage(ImageIO.read(grayscale16));
        filteredImage.getFilters().add(new GrayscaleFilter());
        filteredImage.getFilters().add(new PosterizeFilter());
    }

    @Test
    public void testGrayscale16ToMonochrome2LevelsHasBlackLeftWhiteRight() throws Exception {
        final Waiter waiter = new Waiter();

        ((PosterizeFilter) filteredImage.getFilters().find(PosterizeFilter.class)).setLevels(2);

        filteredImage.addThreadEventListener(new ThreadEventListener() {
            @Override
            public void onThreadComplete() {

            }

            @Override
            public void onRedrawComplete() {
                waiter.resume();
            }
        });
        filteredImage.redraw();

        waiter.await(5000);

        assertEquals("Far left pixel of 2-level posterized grayscale16 should be black",
                new SimplifiedColor(Color.black.getRGB()).toString(),
                new SimplifiedColor(filteredImage.getModifiedImage().getRGB(0, 0)).toString());
        assertEquals("Far right pixel of 2-level posterized grayscale16 should be white",
                new SimplifiedColor(Color.white.getRGB()).toString(),
                new SimplifiedColor(filteredImage.getModifiedImage().getRGB(filteredImage.getModifiedImage().getWidth() - 1, 0)).toString());
    }

    @Test
    public void testGrayscale16ToMonochrome3LevelsHasGrayCenter() throws Exception {
        final Waiter waiter = new Waiter();

        ((PosterizeFilter) filteredImage.getFilters().find(PosterizeFilter.class)).setLevels(3);
        System.out.println(((PosterizeFilter) filteredImage.getFilters().find(PosterizeFilter.class)).getLevelCount());

        filteredImage.addThreadEventListener(new ThreadEventListener() {
            @Override
            public void onThreadComplete() {

            }

            @Override
            public void onRedrawComplete() {
                waiter.resume();
            }
        });
        filteredImage.redraw();

        waiter.await(5000);

        assertEquals("Center pixel of 3-level posterized grayscale16 should be 127, 127, 127",
                new SimplifiedColor(new Color(127, 127, 127).getRGB()).toString(),
                new SimplifiedColor(filteredImage.getModifiedImage().getRGB(filteredImage.getModifiedImage().getWidth() / 2, 0)).toString());
    }

    @Test
    public void testGrayscale16ToMonochrome4LevelsHasGrayCenter() throws Exception {
        final Waiter waiter = new Waiter();

        ((PosterizeFilter) filteredImage.getFilters().find(PosterizeFilter.class)).setLevels(4);

        filteredImage.addThreadEventListener(new ThreadEventListener() {
            @Override
            public void onThreadComplete() {

            }

            @Override
            public void onRedrawComplete() {
                waiter.resume();
            }
        });
        filteredImage.redraw();

        waiter.await(5000);

        assertEquals("2nd block of 4-level posterized grayscale16 should be 85, 85, 85",
                new SimplifiedColor(new Color(85, 85, 85).getRGB()).toString(),
                new SimplifiedColor(filteredImage.getModifiedImage().getRGB(filteredImage.getModifiedImage().getWidth() / 4, 0)).toString());

        assertEquals("3rd block of 4-level posterized grayscale16 should be 170, 170, 170",
                new SimplifiedColor(new Color(170, 170, 170).getRGB()).toString(),
                new SimplifiedColor(filteredImage.getModifiedImage().getRGB(filteredImage.getModifiedImage().getWidth() / 4 * 2, 0)).toString());
    }

}