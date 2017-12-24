package be.howest.photoweave.components;

import be.howest.photoweave.model.properties.BitmapProperties;
import be.howest.photoweave.model.util.CreateFilePicker;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BindingMaker {

    private int PIXEL_SIZE = 40;
    private int X_TILES = 10;
    private int Y_TILES = 10;
    private int W = X_TILES * PIXEL_SIZE;
    private int H = Y_TILES * PIXEL_SIZE;

    private Pixel[][] grid;
    private BufferedImage bindingImage;

    //FXML
    private ObservableList<Integer> bindingSizes = FXCollections.observableArrayList();
    public JFXComboBox<Integer> ComboBoxBindingsSizes;
    public Pane paneBindingCreator;
    public Pane paneImagePreview;
    public AnchorPane anchorPane;


    public BindingMaker() {
    }

    public void initialize() {
        initializeComboBox();
        generateBindingCreator(); // Standard 10 x 10
    }

    private void initializeComboBox() {
        bindingSizes.addAll(10, 9, 8, 7, 6, 5, 4, 3, 2);
        ComboBoxBindingsSizes.setItems(bindingSizes);
        ComboBoxBindingsSizes.getSelectionModel().selectFirst();
        ComboBoxBindingsSizes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                Y_TILES = X_TILES = ComboBoxBindingsSizes.getSelectionModel().getSelectedItem();
                generateBindingCreator();
            }
        });
    }

    private void generateBindingCreator() {
        paneBindingCreator.getChildren().clear();
        paneImagePreview.getChildren().clear();
        paneBindingCreator.setPrefSize(W, H);
        grid = new Pixel[X_TILES][Y_TILES];

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                grid[x][y] = new Pixel(x, y, false, true);;
                paneBindingCreator.getChildren().add(grid[x][y]);
            }
        }
    }



    //FXML
    public void previewBinding() {
        paneImagePreview.getChildren().clear();
        bindingImage = new BufferedImage(X_TILES, Y_TILES,
                BufferedImage.TYPE_INT_RGB);

        int white = 0xFF000000;
        int black = 0xFFFFFFFF;
        for (int index1 = 0; index1 < grid.length; index1++) {
            for (int index2 = 0; index2 < grid[0].length; index2++) {
                if (grid[index1][index2].isFilled) bindingImage.setRGB(index1, index2, white);
                else bindingImage.setRGB(index1, index2, black);
            }
        }

        Image image = SwingFXUtils.toFXImage(bindingImage, null);
        ImagePattern imagePattern = new ImagePattern(image);

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Preview pr = new Preview(i, j, X_TILES, Y_TILES, imagePattern);

                paneImagePreview.getChildren().add(pr);
            }
        }
    }

    public void saveBinding(ActionEvent actionEvent) {
        CreateFilePicker fp = new CreateFilePicker(BitmapProperties.title, (Stage) anchorPane.getScene().getWindow(), BitmapProperties.filterDescription, BitmapProperties.filterExtensions);
        File file = fp.saveFile();

        if (file != null) {
            try {
                ImageIO.write(bindingImage, "bmp", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    // Extra classes
    private class Preview extends StackPane {
        private int x, y;
        private Rectangle rectangle;

        public Preview(int x, int y, int h, int w, ImagePattern imagePattern) {
            this.x = x;
            this.y = y;
            this.rectangle = new Rectangle(w, h);
            this.rectangle.setFill(imagePattern);

            getChildren().addAll(rectangle);

            setTranslateX(x * X_TILES);
            setTranslateY(y * Y_TILES);
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

            rectangle.setFill(Color.WHITE);

            getChildren().addAll(rectangle);

            setTranslateX(x * PIXEL_SIZE);
            setTranslateY(y * PIXEL_SIZE);

            setOnMouseClicked(e -> fill());
        }

        void fill() {
            this.isFilled = !this.isFilled;
            rectangle.setFill((isFilled) ? Color.BLACK : Color.WHITE);
        }
    }
}



