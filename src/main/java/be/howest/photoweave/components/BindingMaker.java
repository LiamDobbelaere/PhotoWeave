package be.howest.photoweave.components;

import be.howest.photoweave.model.weaving.WovenImage;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BindingMaker {

    private static final int PIXEL_SIZE = 40;
    private static final int X_TILES = 10;
    private static final int Y_TILES = 10;

    private static final int W = X_TILES * PIXEL_SIZE;
    private static final int H = Y_TILES * PIXEL_SIZE;

    //private static final int X_TILES = W / PIXEL_SIZE;
    //private static final int Y_TILES = H / PIXEL_SIZE;

    private ObservableList<Integer> sizes = FXCollections.observableArrayList();

    private Pixel[][] grid = new Pixel[X_TILES][Y_TILES];

    public JFXComboBox bindingsizes;
    public Pane bindingpane;
    public Pane imageviewpane;
    public AnchorPane ap;


    public BindingMaker() {
    }

    public void init() {
        sizes.addAll(5, 10);
        bindingsizes.setItems(sizes);
        bindingsizes.getSelectionModel().selectFirst();
        createGrid();
    }

    private void createGrid() {
        bindingpane.setPrefSize(W, H);
        bindingpane.setVisible(true);

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Pixel pixel = new Pixel(x, y, false, true);

                grid[x][y] = pixel;
                bindingpane.getChildren().add(pixel);
            }
        }
    }

    @FXML
    private void preview() {
        BufferedImage bufferedImage = new BufferedImage(10, 10,
                BufferedImage.TYPE_INT_RGB);

        int rgb = 0xFFFFFFFF;
        for (int index1 = 0; index1 < grid.length; index1++) {
            for (int index2 = 0; index2 < grid[0].length; index2++) {
                if (grid[index1][index2].isFilled) bufferedImage.setRGB(index1, index2, rgb);
            }
        }

        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        ImagePattern imagePattern = new ImagePattern(image);

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Preview pr = new Preview(i, j, imagePattern);

                imageviewpane.getChildren().add(pr);
            }
        }
    }

    public void export(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("BMP", ".bmp")
        );
        fileChooser.setTitle("PhotoWeave | Save Binding");
        File file = fileChooser.showSaveDialog((Stage) ap.getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage bufferedImage = new BufferedImage(10, 10,
                        BufferedImage.TYPE_INT_RGB);

                int rgb = 0xFFFFFFFF;
                for (int index1 = 0; index1 < grid.length; index1++) {
                    for (int index2 = 0; index2 < grid[0].length; index2++) {
                        if (grid[index1][index2].isFilled) bufferedImage.setRGB(index1, index2, rgb);
                    }
                }
                ImageIO.write(bufferedImage, "bmp", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private class Preview extends StackPane {
        private int x, y;
        private Rectangle rectangle;

        public Preview(int x, int y, ImagePattern imagePattern) {
            this.x = x;
            this.y = y;
            this.rectangle = new Rectangle(10, 10);
            this.rectangle.setFill(imagePattern);

            getChildren().addAll(rectangle);

            setTranslateX(x * 10);
            setTranslateY(y * 10);
        }
    }

    private class Pixel extends StackPane {
        private int x, y, STROKE_SIZE;
        private boolean isFilled;
        private Rectangle rectangle;

        Pixel(int x, int y, boolean isFilled, boolean hasStroke) {
            this.x = x;
            this.y = y;
            this.isFilled = isFilled;
            this.STROKE_SIZE = (hasStroke) ? 2 : 0;
            this.rectangle = new Rectangle(PIXEL_SIZE - STROKE_SIZE, PIXEL_SIZE - STROKE_SIZE);

            rectangle.setFill(Color.BLACK);

            getChildren().addAll(rectangle);

            setTranslateX(x * PIXEL_SIZE);
            setTranslateY(y * PIXEL_SIZE);

            setOnMouseClicked(e -> fill());
        }

        void fill() {
            this.isFilled = !this.isFilled;
            rectangle.setFill((isFilled) ? Color.WHITE : Color.BLACK);
        }
    }
}



