package be.howest.photoweave.controllers;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class EditPhoto {
    private String path;
    Image img;
    Image newImage;
    @FXML
    ImageView photoview;
    @FXML
    Slider quantizationscale;
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


    public void initData(String path) {
        this.path = path;
        img = new Image("file:" + path);
        photoview.setImage(img);
        fileNameId.setText("File: " + img.getUrl().substring(img.getUrl().lastIndexOf('/') + 1, img.getUrl().length()));
        imageSizeId.setText("Width: " + img.getWidth() + "px; Height: " + img.getHeight() + "px;");
        widthinputtextfield.setText(String.valueOf(img.getWidth()));
        heightinputtextfield.setText(String.valueOf(img.getHeight()));
        zoomPhoto(img);
    }

    private void zoomPhoto(Image img) {
        if (img.getHeight() < img.getWidth()) {
            photoview.setFitHeight(anchorpane.getHeight());
        } else {
            photoview.setFitWidth(anchorpane.getWidth());
        }
    }

    public void sliderMoving() {
        System.out.println(quantizationscale.getValue());
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
        img = new Image(path, Double.parseDouble(widthinputtextfield.getText()), Double.parseDouble(heightinputtextfield.getText()), true, true);
        updateImageView();
    }

    public void updateImageView(){
        photoview.setImage(img);
    }
}
