package be.howest.photoweave.controllers;

import be.howest.photoweave.model.imaging.MonochromeImage;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EditPhoto {
    @FXML
    ImageView photoview;
    @FXML
    Slider slider;
    @FXML
    VBox imagevbox;
    @FXML
    AnchorPane anchorpane;
    @FXML
    Label fileNameId;
    @FXML
    Label imageSizeId;
    @FXML
    TextField widthinputtextfield;
    @FXML
    TextField heightinputtextfield;

    //Image parameters
    private String path;
    double imageWidth;
    double imageHeight;
    String filename;
    BufferedImage img;
    MonochromeImage monochromeImg;
    Image endImage;

    //Edit parameters
    int posterizeScale;


    public void initData(String path) throws IOException {
        //init parameters
        this.path = path;
        this.img = ImageIO.read(new File(path));
        this.monochromeImg = new MonochromeImage(img);
        this.imageWidth = img.getWidth();
        this.imageHeight = img.getHeight();
        this.filename = path.substring(path.lastIndexOf('/') + 1, path.length());
        this.posterizeScale = 10;

        //set properties
        fileNameId.setText("File: " + filename);
        imageSizeId.setText("Width: " + imageWidth + "px; Height: " + imageHeight + "px;");
        widthinputtextfield.setText(String.valueOf(imageWidth));
        heightinputtextfield.setText(String.valueOf(imageHeight));

        updateImage();

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                posterizeScale = new_val.intValue();
                updateImage();
            }
        });
        widthinputtextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            imageWidth = Integer.parseInt(newValue);
            updateImage();
        });
        heightinputtextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            imageHeight = Integer.parseInt(newValue);
            updateImage();
        });
    }

    private void zoomPhoto(Image img) {
        if (img.getHeight() < img.getWidth()) {
            photoview.setFitHeight(anchorpane.getHeight());
        } else {
            photoview.setFitWidth(anchorpane.getWidth());
        }
    }

    public void zoomin() {
        photoview.setFitWidth(photoview.getFitWidth() * 1.3);
        photoview.setFitHeight(photoview.getFitHeight() * 1.3);
    }

    public void zoomout() {
        photoview.setFitWidth(photoview.getFitWidth() / 1.3);
        photoview.setFitHeight(photoview.getFitHeight() / 1.3);
    }


    public void sizeChanged(Observable a, String b, String c) {
        double height = Double.parseDouble(b);
        double width = Double.parseDouble(c);
        //newImage = new Image(path, Double.parseDouble(widthinputtextfield.getText()), Double.parseDouble(heightinputtextfield.getText()), true, true);
        //updateImageView(newImage);
    }

    public void updateImage() {
        monochromeImg.setLevels(posterizeScale);
        monochromeImg.redraw();



        endImage = SwingFXUtils.toFXImage(monochromeImg.getModifiedImage(), null);
        updateImageView(endImage);
    }

    public void updateImageView(Image image) {
        photoview.setImage(image);
        zoomPhoto(image);
    }
}
