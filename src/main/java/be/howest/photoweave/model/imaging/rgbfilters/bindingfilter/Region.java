package be.howest.photoweave.model.imaging.rgbfilters.bindingfilter;

import be.howest.photoweave.model.binding.Binding;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tomdo on 6/12/2017.
 */
public class Region {
    private int minX;
    private int minY;
    private int width;
    private int height;
    private boolean[][] region;

    private boolean marked;
    private int targetLevel;
    private Binding targetBinding;

    public Region(List<Point> selection) {
        int minX = Integer.MAX_VALUE;
        int maxX = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;

        for (Point point : selection) {
            if (point.x < minX) minX = point.x;
            if (point.x > maxX) maxX = point.x;
            if (point.y < minY) minY = point.y;
            if (point.y > maxY) maxY = point.y;
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
        this.region = new boolean[height][width];
        this.marked = false;
        this.targetLevel = 0;

        addSelectionToRegion(selection);
        autoFill();
    }

    private void addSelectionToRegion(List<Point> selection) {
        for (Point point : selection) {
            this.region[point.y - minY][point.x - minX] = true;
        }
    }

    private void autoFill() {
        if (this.width == 0 || this.height == 0) return;

        int fillX = 0;
        int fillY = 0;

        for (int y = 0; y < this.height - 1; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.region[y][x] && !this.region[y + 1][x]) {
                    fillX = x;
                    fillY = y + 1;

                    //Get out of the loop
                    x = this.width;
                    y = this.height;
                }

            }
        }

        fill(fillX, fillY);
    }

    private void fill(int x, int y) {
        LinkedList<Point> queue = new LinkedList<>();

        queue.push(new Point(x, y));

        while (!queue.isEmpty()) {
            Point point = queue.pop();

            this.region[point.y][point.x] = true;

            if (canColor(point.x + 1, point.y))
                queue.push(new Point(point.x + 1, point.y));

            if (canColor(point.x - 1, point.y))
                queue.push(new Point(point.x - 1, point.y));

            if (canColor(point.x, point.y + 1))
                queue.push(new Point(point.x, point.y + 1));

            if (canColor(point.x, point.y - 1))
                queue.push(new Point(point.x, point.y - 1));
        }
    }

    private boolean canColor(int x, int y) {
        return (x >= 0 && y >= 0 && x < this.width && y < this.height) && !this.region[y][x];
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean[][] getRegion() {
        return region;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(int targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Binding getTargetBinding() {
        return targetBinding;
    }

    public void setTargetBinding(Binding targetBinding) {
        this.targetBinding = targetBinding;
    }
}
