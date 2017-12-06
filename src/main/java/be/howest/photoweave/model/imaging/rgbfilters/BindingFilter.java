package be.howest.photoweave.model.imaging.rgbfilters;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.PrimitiveUtil;

import java.awt.Point;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomdo on 9/11/2017.
 */
public class BindingFilter implements RGBFilter {
    private PosterizeFilter posterizeFilter;
    private BindingFactory bindingFactory;
    private Map<Integer, Binding> bindingsMap;

    private boolean inverted;
    private boolean showMarkedBinding;
    private Binding markedBinding;

    private FilteredImage filteredImage;

    private Region region;

    public BindingFilter(PosterizeFilter posterizeFilter, FilteredImage filteredImage) {
        this.bindingFactory = new BindingFactory();
        this.posterizeFilter = posterizeFilter;
        this.filteredImage = filteredImage;
        this.bindingsMap = new HashMap<>();
        this.region = new Region(0, 0, 0, 0, new ArrayList<>());
    }

    public Map<Integer, Binding> getBindingsMap() {
        return bindingsMap;
    }

    private int findBestBindingForLevel(int level) {
        return level * ((bindingFactory.getOptimizedBindings().length - 1) / (this.posterizeFilter.getLevelCount() - 1));
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public void setShowMarkedBinding(boolean showMarkedBinding) {
        this.showMarkedBinding = showMarkedBinding;
    }

    public void setMarkedBinding(Binding markedBinding) {
        this.markedBinding = markedBinding;
    }

    public PosterizeFilter getPosterizeFilter() {
        return posterizeFilter;
    }

    public void addRegion(List<Point> selection) {
        int minX = Integer.MAX_VALUE;
        int maxX = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;

        for (Point point : selection) {
            if (point.x < minX) {
                minX = point.x;
            }

            if (point.x > maxX) {
                maxX = point.x;
            }

            if (point.y < minY) {
                minY = point.y;
            }

            if (point.y > maxY) {
                maxY = point.y;
            }
        }

        System.out.println(String.format("%s, %s, %s, %s", minX, minY, maxX, maxY));


        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        this.region = new Region(minX, minY, width, height, selection);

        /*regions = new int[selection.size() * 2];

        for (int i = 0; i < selection.size() * 2; i+=2) {
            Point point = selection.get(i / 2);

            regions[i] = point.x;
            regions[i + 1] = point.y;
        }*/
    }

    @Override
    public int applyTo(int rgb, int i, byte[] imageMetaData) {
        int currentLevel = (int) Math.floor(((rgb >> 16) & 0xff) / (255.0 / (this.posterizeFilter.getLevelCount() - 1)));

        int fullX = i % this.filteredImage.getModifiedImage().getWidth();
        int fullY = ((int) Math.floor(i / this.filteredImage.getModifiedImage().getWidth()));

        Binding binding = this.bindingsMap.computeIfAbsent(
                currentLevel, level -> bindingFactory.getOptimizedBindings()[findBestBindingForLevel(level)]);

        boolean[][] region = this.region.getRegion();

        if (fullX >= this.region.getMinX() && fullY >= this.region.getMinY() &&
                fullX <= this.region.getMinX() + this.region.getWidth() &&
                fullY <= this.region.getMinY() + this.region.getHeight()) {

            for (int cy = 0; cy < this.region.getHeight(); cy++) {
                for (int cx = 0; cx < this.region.getWidth(); cx++) {

                    if (cx == fullX - this.region.getMinX() && cy == fullY - this.region.getMinY()) {
                        if (imageMetaData[0] == 0 && region[cy][cx]) {
                            //Todo: the 0 should actually be whatever level is assigned to the region
                            //Todo: the 1 should be whatever other binding is assigned to the region
                            
                            binding = this.bindingsMap.get(1);
                        }
                    }
                }
            }

        }


        BufferedImage pattern = binding.getBindingImage(); //binding.getBindingImage();

        int x = fullX % pattern.getWidth();
        int y = fullY % pattern.getHeight();
        int color = pattern.getRGB(x, y);


        if (inverted) {
            if (color == Color.BLACK.getRGB()) color = Color.WHITE.getRGB();
            else color = Color.BLACK.getRGB();
        }

        if (markedBinding != null && showMarkedBinding && binding == markedBinding) {
            if (color == Color.BLACK.getRGB()) color = Color.YELLOW.getRGB();
            else color = Color.LIGHT_GRAY.getRGB();
        }

        /*for (int j = 0; j < regions.length; j+=2) {
            if (regions[j] == fullX && regions[j + 1] == fullY)
                color = Color.GREEN.getRGB();

        }*/

        /*for (List<Point> selection : regions) {
            for (Point point : selection) {
                if (point.x == fullX && point.y == fullY) {
                    if (imageMetaData[0] == 0)
                        color = Color.GREEN.getRGB();
                }
            }
        }*/

        return color;
    }
}
