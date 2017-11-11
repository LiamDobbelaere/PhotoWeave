package be.howest.photoweave.controllers;

import be.howest.photoweave.components.BindingMaker;
import be.howest.photoweave.components.PixelatedImageView;
import be.howest.photoweave.components.SelectBinding;
import be.howest.photoweave.components.events.BindingChangedEventHandler;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.ThreadEventListener;
import be.howest.photoweave.model.imaging.filters.BindingFilter;
import be.howest.photoweave.model.imaging.filters.GrayscaleFilter;
import be.howest.photoweave.model.imaging.filters.PosterizeFilter;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EditPhoto implements ThreadEventListener {
    /* FXML User Interface */
    public AnchorPane anchorPaneWindow;
    public Label labelFileNameId;
    public Label labelImageSizeId;

    public Slider sliderPosterizationScale;
    public Label labelAmountOfColors;
    public TextField textFieldWidth;
    public TextField textFieldHeight;
    public SelectBinding vboxSelectBinding;
    public JFXCheckBox checkBoxMarkBinding;
    public JFXCheckBox checkBoxInvert;
    public JFXCheckBox checkBoxFloaters;
    public JFXTextField textFieldXFloaters;
    public JFXTextField textFieldYFloaters;

    public AnchorPane anchorPanePhotoView;
    public VBox vboxPhotView;
    public PixelatedImageView photoView;

    /*  */
    private int imageWidth;
    private int imageHeight;
    private String filename;
    private BufferedImage image;
    private BufferedImage originalImage;
    private FilteredImage filteredImage;
    private Stage stage;

    private int posterizeScale = 10;

    public void initialize(String path) throws IOException {
        // Logic
        this.image = ImageIO.read(new File(path));
        this.originalImage = image;
        this.filteredImage = new FilteredImage(image);
        filteredImage.addThreadEventListener(this);
        filteredImage.getFilters().add(new GrayscaleFilter());
        filteredImage.getFilters().add(new PosterizeFilter());
        filteredImage.getFilters().add(new BindingFilter(
                (PosterizeFilter) filteredImage.getFilters().find(PosterizeFilter.class), filteredImage));

        this.posterizeScale = 10;

        // UI
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.filename = path.substring(path.lastIndexOf("/") + 1);

        // Global
        this.stage = (Stage) anchorPaneWindow.getScene().getWindow();

        initializeListeners();
        initializePhotoScale();
        updateTexts();
    }

    /* UI */
    private void initializePhotoScale() {
        stage.show();
        if (image.getHeight() <= image.getWidth()) {
            photoView.setFitWidth(anchorPanePhotoView.getWidth());
        } else {
            photoView.setFitHeight(anchorPanePhotoView.getHeight());
        }
        updateImage();
    }

    private void updateTexts() {
        labelFileNameId.setText("File: " + filename);
        labelImageSizeId.setText("Width: " + imageWidth + "px; Height: " + imageHeight + "px;");
        textFieldWidth.setText(String.valueOf(imageWidth));
        textFieldHeight.setText(String.valueOf(imageHeight));
        labelAmountOfColors.setText("Amount of colors: " + posterizeScale);
    }

    private void redrawPhotoView() {
        photoView.setImage(SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null));
    }

    private void resizeImage() {
        filteredImage.resize(imageWidth, imageHeight);

        updateImage();
    }

    private void updateImage() {
        filteredImage.redraw();
        updateTexts();
    }

    /* FXML Hooks */
    public void zoomIn() {
        photoView.setFitWidth(photoView.getFitWidth() * 1.3);
        photoView.setFitHeight(photoView.getFitHeight() * 1.3);
    }

    public void zoomOut() {
        photoView.setFitWidth(photoView.getFitWidth() / 1.3);
        photoView.setFitHeight(photoView.getFitHeight() / 1.3);
    }

    public void saveImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", ".png"),
                new FileChooser.ExtensionFilter("JPG", ".jpg"),
                new FileChooser.ExtensionFilter("JPEG", ".jpeg")
        );
        fileChooser.setTitle("PhotoWeave | Save Image");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                //Kan hier een confict zijn.
                filteredImage.redraw();

                ImageIO.write(filteredImage.getModifiedImage(), "png", file);
            } catch (IOException ex) {
            }
        }
    }

    public void openBindingCreator(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("components/BindingMaker.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(loader.load()));
        stage.setMinHeight(600.0);
        stage.setMinWidth(800.0);
        stage.setTitle("PhotoWeave | Edit Photo");
        stage.getIcons().add(new Image("logo.png"));

        BindingMaker controller = loader.getController();
        controller.initialize();
        stage.show();
    }


    /* Event Handlers */
    private void initializeListeners() {
        /* FXML */
        sliderPosterizationScale
                .setOnMouseReleased(this::updatePosterizationLevelOnImage);

        textFieldWidth
                .focusedProperty()
                .addListener(this::ResizeImageHeight);
        textFieldHeight
                .focusedProperty()
                .addListener(this::ResizeImageWidth);
        textFieldHeight
                .textProperty()
                .addListener(this::ChangeImageHeight);
        textFieldWidth
                .textProperty()
                .addListener(this::ChangeImageWidth);
        checkBoxMarkBinding
                .selectedProperty()
                .addListener(this::showMarkingOnImageView);
        checkBoxInvert
                .selectedProperty()
                .addListener(this::InvertColorsInWovenImage);
        checkBoxFloaters
                .selectedProperty()
                .addListener(this::ShowFloatersOnImageView);
        textFieldXFloaters
                .textProperty()
                .addListener(this::ChangeXFloatersThreshold);
        textFieldYFloaters
                .textProperty()
                .addListener(this::ChangeYFloatersThreshold);

        anchorPaneWindow
                .heightProperty()
                .addListener(this::ResizeImageViewHeight);
        anchorPaneWindow
                .widthProperty()
                .addListener(this::ResizeImageViewWidth);

        /* CUSTOM */
        /*vboxSelectBinding
                .getComboBox()
                .addEventHandler(BindingChanged.BINDING_CHANGED, this.changeBindingInWomenImage());

        vboxSelectBinding
                .getComboBoxColors()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(this::MarkColorOnImageView);*/
    }

    private void updatePosterizationLevelOnImage(MouseEvent mouseEvent) {
        posterizeScale = sliderPosterizationScale.valueProperty().intValue();
        labelAmountOfColors.setText("Amount of colors: " + posterizeScale);

        PosterizeFilter posterizeFilter = (PosterizeFilter) filteredImage.getFilters().find(PosterizeFilter.class);
        posterizeFilter.setLevels(posterizeScale);

        updateImage();
    }

    private void ResizeImageViewHeight(Observable observable, Number oldValue, Number newValue) {
        vboxPhotView.setMinHeight((Double) newValue - 50);
    }

    private void ResizeImageViewWidth(Observable observable, Number oldValue, Number newValue) {
        vboxPhotView.setMinWidth((Double) newValue - 200);
    }

    private void ChangeImageWidth(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            textFieldWidth.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!textFieldWidth.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                imageWidth = Integer.parseInt(newValue);
            }
        }
    }

    private void ChangeImageHeight(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!textFieldHeight.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                imageHeight = Integer.parseInt(newValue);
            }
        }
    }

    private void showMarkingOnImageView(Observable observable) {
        Integer selectedBinding = vboxSelectBinding.getComboBoxColors().getSelectionModel().getSelectedItem();

        if (selectedBinding == null) selectedBinding = vboxSelectBinding.getComboBoxColors().getItems().get(0);

        //todo: Change to filter
        /*wovenImage.setMarkedBinding(selectedBinding);
        wovenImage.setShowMarkedBinding(checkBoxMarkBinding.isSelected());
        wovenImage.redraw();*/
    }

    private void InvertColorsInWovenImage(Observable observable, Boolean oldValue, Boolean newValue) {
        //todo: change to filter

        BindingFilter bindingFilter = (BindingFilter) filteredImage.getFilters().find(BindingFilter.class);
        bindingFilter.setInverted(newValue);
        filteredImage.redraw();
    }

    private void ShowFloatersOnImageView(Observable observable, Boolean oldValue, Boolean newValue) {
        //todo: change to filter
        //wovenImage.setShowFloaters(newValue);
        //wovenImage.redraw();
    }

    private void ChangeXFloatersThreshold(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!textFieldXFloaters.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                //todo: change to filter
                //wovenImage.setFloaterTresholdX(Integer.parseInt(newValue));
                //wovenImage.redraw();
                redrawPhotoView();
            }
        }
    }

    private void ChangeYFloatersThreshold(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!textFieldYFloaters.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                //todo: change to filter
                //wovenImage.setFloaterTresholdY(Integer.parseInt(newValue));
                //wovenImage.redraw();
                redrawPhotoView();
            }
        }
    }

    private void ResizeImageWidth(Observable observable, Boolean oldValue, Boolean newValue) {
        if (!newValue && (imageHeight != image.getHeight() || imageWidth != image.getWidth())) {
            resizeImage();
        }
    }

    private void ResizeImageHeight(Observable observable, Boolean oldValue, Boolean newValue) {
        if (!newValue && (imageHeight != image.getHeight() || imageWidth != image.getWidth())) {
            resizeImage();
        }
    }

    private BindingChangedEventHandler changeBindingInWomenImage() {
        return new BindingChangedEventHandler() {
            @Override
            public void onBindingChanged() {
                //todo: change to filter
                //wovenImage.redraw();
                redrawPhotoView();
            }
        };
    }

    private void MarkColorOnImageView(Observable observable) {
        //todo: change to filter
        //wovenImage.setMarkedBinding(vboxSelectBinding.getComboBoxColors().getSelectionModel().getSelectedItem());
        //wovenImage.setShowMarkedBinding(checkBoxMarkBinding.isSelected());
        //wovenImage.redraw();
        redrawPhotoView();
    }

    @Override
    public void onThreadComplete() {
        redrawPhotoView();
    }

    @Override
    public void onRedrawComplete() {
        redrawPhotoView();
    }
}