package be.howest.photoweave.controllers;

import be.howest.photoweave.components.BindingMaker;
import be.howest.photoweave.components.PixelatedImageView;
import be.howest.photoweave.components.SelectBinding;
import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.components.events.BindingChangedEventHandler;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.ThreadEventListener;
import be.howest.photoweave.model.imaging.imagefilters.FloatersFilter;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.GrayscaleFilter;
import be.howest.photoweave.model.imaging.rgbfilters.PosterizeFilter;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.svg.SVGGlyph;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public VBox vboxPhotoView;
    public PixelatedImageView photoView;
    public TitledPane paneDefault;
    public Label filePath;
    public JFXButton toggleEditButton;
    public ScrollPane imageScrollPane;

    /*  */
    private int imageWidth;
    private int imageHeight;
    private String filename;
    private BufferedImage image;
    private BufferedImage originalImage;
    private FilteredImage filteredImage;
    private Stage stage;

    private BindingChangedEventHandler bindingChangedEventHandler;
    private ChangeListener<Integer> markedColorChangeListener;

    private int posterizeScale = 10;

    private int pXStart = -1;
    private int pYStart = -1;
    private int pXPrevious = -1;
    private int pYPrevious = -1;

    private boolean editing = false;
    private java.util.List<Point> selectionPoints;

    public void initialize(String path) throws IOException {
        // Logic
        this.image = ImageIO.read(new File(path));
        this.originalImage = image;
        this.filteredImage = new FilteredImage(image);
        this.filteredImage.addThreadEventListener(this);
        this.filteredImage.getFilters().add(new GrayscaleFilter());
        this.filteredImage.getFilters().add(new PosterizeFilter());
        this.filteredImage.getFilters().add(new BindingFilter(
                (PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class), filteredImage));
        this.filteredImage.getFilters().add(new FloatersFilter(checkBoxFloaters.selectedProperty().get()));

        this.vboxSelectBinding.setBindingFilter((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class));

        this.posterizeScale = 10;

        this.bindingChangedEventHandler = new BindingChangedEventHandler() {
            @Override
            public void onBindingChanged() {
                System.out.println("BINDING CHANGED");

                filteredImage.redraw();
            }
        };

        this.markedColorChangeListener = (observable, oldValue, newValue) -> {
            this.MarkColorOnImageView(observable);
        };

        // UI
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.filename = path.substring(path.lastIndexOf("/") + 1);

        // Global
        this.stage = (Stage) anchorPaneWindow.getScene().getWindow();

        initializeListeners();
        initializePhotoScale();
        updateTexts();

        paneDefault.setExpanded(true);
    }

    /* UI */
    private void initializePhotoScale() {
        if (image.getHeight() <= image.getWidth())
            photoView.setFitWidth(vboxPhotoView.getWidth() - 2);
        else
            photoView.setFitHeight(vboxPhotoView.getHeight() - 2);


        updateImage();
    }

    private void updateTexts() {
        labelFileNameId.setText(new File(filename).getName());
        filePath.setText(filename);
        textFieldWidth.setText(String.valueOf(imageWidth));
        textFieldHeight.setText(String.valueOf(imageHeight));
        labelAmountOfColors.setText("Aantal tinten: " + posterizeScale);
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
        System.out.println("4; " + photoView.getFitWidth());
    }

    public void zoomOut() {
        photoView.setFitWidth(photoView.getFitWidth() / 1.3);
        photoView.setFitHeight(photoView.getFitHeight() / 1.3);
    }

    public void fitWindow(ActionEvent actionEvent) {
        if (image.getHeight() <= image.getWidth())
            photoView.setFitWidth(vboxPhotoView.getWidth() - 2);
        else
            photoView.setFitHeight(vboxPhotoView.getHeight() - 2);
    }

    public void saveImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("BMP", ".bmp")
        );
        fileChooser.setTitle("PhotoWeave | Save Image");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                //Kan hier een confict zijn.

                ImageIO.write(ImageUtil.convertImageToByteBinary(filteredImage.getModifiedImage()), "bmp", file);
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
        vboxSelectBinding
                .getComboBoxBindings()
                .addEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);

        vboxSelectBinding
                .getComboBoxLevels()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(this.markedColorChangeListener);

        photoView
                .setOnMouseDragged(ManipulatePixel());
        photoView.setOnMousePressed((event) -> {
            BufferedImage bi = SwingFXUtils.fromFXImage(photoView.getImage(), null);

            double xPercent = event.getX() / photoView.getBoundsInParent().getWidth();
            double yPercent = event.getY() / photoView.getBoundsInParent().getHeight();

            int pX = (int) (bi.getWidth() * xPercent);
            int pY = (int) (bi.getHeight() * yPercent);

            pXStart = pX;
            pYStart = pY;

            selectionPoints = new ArrayList<>();
        });
        photoView.setOnMouseReleased((event) -> {
            BufferedImage bi = SwingFXUtils.fromFXImage(photoView.getImage(), null);

            drawLine(bi, pXStart, pYStart, pXPrevious, pYPrevious);

            pXPrevious = -1;
            pYPrevious = -1;

            photoView.setImage(SwingFXUtils.toFXImage(bi, null));

            BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
            bf.addRegion(selectionPoints);

            filteredImage.redraw();
        });
    }

    private void updatePosterizationLevelOnImage(MouseEvent mouseEvent) {
        posterizeScale = sliderPosterizationScale.valueProperty().intValue();
        labelAmountOfColors.setText("Amount of colors: " + posterizeScale);

        PosterizeFilter posterizeFilter = (PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class);
        posterizeFilter.setLevels(posterizeScale);

        BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        bf.getBindingsMap().clear();

        updateImage();
    }

    private void ResizeImageViewHeight(Observable observable, Number oldValue, Number newValue) {
        vboxPhotoView.setMinHeight((Double) newValue - 50);
    }

    private void ResizeImageViewWidth(Observable observable, Number oldValue, Number newValue) {
        vboxPhotoView.setMinWidth((Double) newValue - 200);
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
        BindingFilter bindingFilter = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        Binding selectedBinding = bindingFilter.getBindingsMap().get(vboxSelectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem());

        if (selectedBinding == null)
            selectedBinding = bindingFilter.getBindingsMap().get(vboxSelectBinding.getComboBoxLevels().getItems().get(0));

        bindingFilter.setMarkedBinding(selectedBinding);
        bindingFilter.setShowMarkedBinding(checkBoxMarkBinding.isSelected());
        filteredImage.redraw();
    }

    private void InvertColorsInWovenImage(Observable observable, Boolean oldValue, Boolean newValue) {
        BindingFilter bindingFilter = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        bindingFilter.setInverted(newValue);
        filteredImage.redraw();
    }

    private void ShowFloatersOnImageView(Observable observable, Boolean oldValue, Boolean newValue) {
        FloatersFilter floatersFilter =
                (FloatersFilter) filteredImage.getFilters().findImageFilter(FloatersFilter.class);

        floatersFilter.setEnabled(newValue);

        filteredImage.redraw();
    }

    private void ChangeXFloatersThreshold(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!textFieldXFloaters.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                FloatersFilter floatersFilter =
                        (FloatersFilter) filteredImage.getFilters().findImageFilter(FloatersFilter.class);

                floatersFilter.setFloaterTresholdX(Integer.parseInt(newValue));

                filteredImage.redraw();
            }
        }
    }

    private void ChangeYFloatersThreshold(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!textFieldYFloaters.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                FloatersFilter floatersFilter =
                        (FloatersFilter) filteredImage.getFilters().findImageFilter(FloatersFilter.class);

                floatersFilter.setFloaterTresholdY(Integer.parseInt(newValue));

                filteredImage.redraw();
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

    private BindingChangedEventHandler changeBindingInWovenImage() {
        return this.bindingChangedEventHandler;
    }

    private void MarkColorOnImageView(Observable observable) {
        BindingFilter bindingFilter = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);

        bindingFilter.setMarkedBinding(bindingFilter.getBindingsMap().get(
                vboxSelectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem()));
        bindingFilter.setShowMarkedBinding(checkBoxMarkBinding.isSelected());
        filteredImage.redraw();
        System.out.println("MARK COLOR ON IMAGE VIEW");
    }

    @Override
    public void OnRedrawBegin() {
        vboxSelectBinding
                .getComboBoxLevels()
                .getSelectionModel()
                .selectedItemProperty()
                .removeListener(this.markedColorChangeListener);
        vboxSelectBinding.removeEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);
    }

    @Override
    public void onThreadComplete() {
        //You could add a waiting symbol here
        //redrawPhotoView(); //Optional, but shows the thread's progress in realtime
    }

    @Override
    public void onRedrawComplete() {
        Platform.runLater(
                () -> {
                    vboxSelectBinding.setBindingFilter(
                            ((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class)));

                    vboxSelectBinding
                            .getComboBoxLevels()
                            .getSelectionModel()
                            .selectedItemProperty()
                            .addListener(this.markedColorChangeListener);
                    vboxSelectBinding.addEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);

                    redrawPhotoView();
                });
    }

    public void ShowCalculateWindow(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/CalculateFlattening.fxml"));

        Scene scene = null;

        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CalculateFlattening controller = loader.getController();
        controller.initialize(this.filteredImage);

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("Afplatting berekenen");
        stage.setScene(scene);
        stage.initOwner(this.stage.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        textFieldWidth.textProperty().setValue(String.valueOf(filteredImage.getModifiedImage().getWidth()));
        textFieldHeight.textProperty().setValue(String.valueOf(filteredImage.getModifiedImage().getHeight()));

        filteredImage.redraw();
    }

    private void drawLine(BufferedImage bi, int x1, int y1, int x2, int y2) {
        // delta of exact value and rounded value of the dependent variable
        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;

        if (dx >= dy) {
            while (true) {
                bi.setRGB(x, y, Color.RED.getRGB());
                selectionPoints.add(new Point(x, y));
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            while (true) {
                bi.setRGB(x, y, Color.RED.getRGB());
                selectionPoints.add(new Point(x, y));
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
    }

    private EventHandler<MouseEvent> ManipulatePixel() {
        return event -> {
            if (!editing) return;

            BufferedImage bi = SwingFXUtils.fromFXImage(photoView.getImage(), null);

            double xPercent = event.getX() / photoView.getBoundsInParent().getWidth();
            double yPercent = event.getY() / photoView.getBoundsInParent().getHeight();

            int pX = (int) (bi.getWidth() * xPercent);
            int pY = (int) (bi.getHeight() * yPercent);
            int original = bi.getRGB(pX, pY);

            if (pXPrevious == -1 || pYPrevious == -1) {
                bi.setRGB(pX, pY, Color.RED.getRGB());
                selectionPoints.add(new Point(pX, pY));
            } else {
                drawLine(bi, pXPrevious, pYPrevious, pX, pY);
            }

            //bi.setRGB(pX, pY, original == Color.BLACK.getRGB() ? Color.WHITE.getRGB() : Color.BLACK.getRGB());

            pXPrevious = pX;
            pYPrevious = pY;

            photoView.setImage(SwingFXUtils.toFXImage(bi, null));
        };
    }

    public void toggleEdit(ActionEvent actionEvent) {
        editing = !editing;

        if (editing) {
            toggleEditButton.setStyle("-fx-background-color: -app-color-secondary;");
            imageScrollPane.setPannable(false);
        } else {
            toggleEditButton.setStyle("");
            imageScrollPane.setPannable(true);
        }

    }
}