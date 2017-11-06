package be.howest.photoweave.components;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class BindingMaker {

    private static final int PIXEL_SIZE = 40;
    private static final int W = 800;
    private static final int H = 600;

    //private static final int X_TILES = W / PIXEL_SIZE;
    //private static final int Y_TILES = H / PIXEL_SIZE;

    private static final int X_TILES = 10;
    private static final int Y_TILES = 10;
    @FXML
    public ImageView imageview;
    public Pane bindingpane;

    private Pixel[][] grid = new Pixel[X_TILES][Y_TILES];
    private Scene scene;

    public BindingMaker() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("components/BindingMaker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        bindingpane
    }

    public void initialize(Stage stage) throws Exception {
        //scene = new Scene(createGrid());
        //createGrid();
        //stage.setScene(scene);
        //stage.show();
    }

    private Parent createGrid() {
        Pane root = new Pane();
        root.setPrefSize(W, H);

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Pixel pixel = new Pixel(x, y, false, true);

                grid[x][y] = pixel;
                root.getChildren().add(pixel);
            }
        }
        //bindingpane = root;
        return root;
    }

    private void export(){
        BufferedImage bufferedImage = new BufferedImage(10, 10,
                BufferedImage.TYPE_INT_RGB);

        int rgb = 0xFF00FF00;
        for (int index1 = 0; index1 < grid.length; index1++){
            for (int index2 = 0; index2 < grid[0].length; index2++){
                if (grid[index1][index2].isFilled)bufferedImage.setRGB(index1, index2, rgb);
            }
        }
        imageview.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
    }

    private class Pixel extends StackPane {
        private int x, y, STROKE_SIZE;
        private boolean isFilled;
        private Rectangle rectangle;

        public Pixel(int x, int y, boolean isFilled, boolean hasStroke) {
            this.x = x;
            this.y = y;
            this.isFilled = isFilled;
            this.STROKE_SIZE = (hasStroke)? 2 : 0;
            this.rectangle = new Rectangle(PIXEL_SIZE - STROKE_SIZE, PIXEL_SIZE - STROKE_SIZE);

            rectangle.setFill(Color.BLACK);

            getChildren().addAll(rectangle);

            setTranslateX(x * PIXEL_SIZE);
            setTranslateY(y * PIXEL_SIZE);

            setOnMouseClicked(e -> fill());
        }

        public void fill(){
            //rectangle.setFill(Color.RED);
            this.isFilled = !this.isFilled;
            rectangle.setFill((isFilled)? Color.WHITE : Color.BLACK);
            export();
        }
    }
}



