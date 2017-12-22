package be.howest.photoweave.controllers;

import be.howest.photoweave.components.SelectionListCell;
import be.howest.photoweave.components.ColorBindingLinker;
import be.howest.photoweave.components.PixelatedImageView;
import be.howest.photoweave.components.SelectBinding;
import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.components.events.BindingChangedEventHandler;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.properties.*;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.ThreadEventListener;
import be.howest.photoweave.model.imaging.imagefilters.FloatersFilter;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.GrayscaleFilter;
import be.howest.photoweave.model.imaging.rgbfilters.PosterizeFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.CreateFilePicker;
import be.howest.photoweave.model.util.CreateWindow;
import be.howest.photoweave.model.util.ImageUtil;
import be.howest.photoweave.model.util.PrimitiveUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

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
    public StackPane contentStackpane;
    public Canvas selectionCanvas;
    public JFXButton togglePickerButton;
    public Label sizeWarning;
    public JFXListView<Region> selectionsList;

    /*  */
    private int imageWidth;
    private int imageHeight;
    private String filename;
    private BufferedImage image;
    private FilteredImage filteredImage;
    private Stage stage;

    private BindingChangedEventHandler bindingChangedEventHandler;
    private ChangeListener<Integer> markedColorChangeListener;
    private ChangeListener<Boolean> showMarkingChangeListener;

    private int posterizeScale = 10;

    private int pXStart = -1;
    private int pYStart = -1;
    private int pXPrevious = -1;
    private int pYPrevious = -1;

    private double pXStartSelection = -1;
    private double pYStartSelection = -1;
    private double pXPreviousSelection = -1;
    private double pYPreviousSelection = -1;

    private boolean editing = false;
    private boolean picking = false;

    private java.util.List<Point> selectionPoints;
    private WritableImage writablePhotoview;
    private WritableImage writableSelection;

    public void initialize(String path) throws IOException {
        // Logic
        this.image = ImageIO.read(new File(path));
        this.filteredImage = new FilteredImage(image);
        this.filteredImage.addThreadEventListener(this);
        this.filteredImage.getFilters().add(new GrayscaleFilter());
        this.filteredImage.getFilters().add(new PosterizeFilter());
        this.filteredImage.getFilters().add(new BindingFilter(
                (PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class), filteredImage));
        this.filteredImage.getFilters().add(new FloatersFilter(checkBoxFloaters.selectedProperty().get()));

        /* nodig ?
        this.vboxSelectBinding.setBindingsMap(((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class)).getBindingsMap(),
                (((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class))));*/

        ((PosterizeFilter) this.filteredImage.getFilters().findRGBFilter(PosterizeFilter.class))
                .setLevels((int) sliderPosterizationScale.getValue());

        FloatersFilter floatersFilter = (FloatersFilter) this.filteredImage.getFilters().findImageFilter(FloatersFilter.class);
        floatersFilter.setFloaterTresholdX(Integer.parseInt(textFieldXFloaters.textProperty().getValue()));
        floatersFilter.setFloaterTresholdY(Integer.parseInt(textFieldYFloaters.textProperty().getValue()));

        this.vboxSelectBinding.setBindingsMap(((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class)).getBindingsMap(),
                (((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class))));

        this.posterizeScale = 10;

        this.bindingChangedEventHandler = new BindingChangedEventHandler() {
            @Override
            public void onBindingChanged() {
                filteredImage.redraw();
            }
        };

        this.markedColorChangeListener = (observable, oldValue, newValue) -> {
            this.MarkColorOnImageView(observable);
        };

        this.showMarkingChangeListener = (observable, oldValue, newValue) -> {
            showMarkingOnImageView();
        };

        // UI
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.filename = path.substring(path.lastIndexOf("/") + 1);
        this.selectionsList.setCellFactory(param -> new SelectionListCell<>(filteredImage));
        this.selectionsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            BindingFilter bf = (BindingFilter) this.filteredImage.getFilters().findRGBFilter(BindingFilter.class);

            bf.getRegions().forEach(region -> region.setMarked(false));

            newValue.setMarked(true);

            filteredImage.redraw();
        });

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
        writablePhotoview = SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null);
        photoView.setImage(writablePhotoview);

        int overlayWidth = (int) imageScrollPane.getViewportBounds().getWidth();
        int overlayHeight = (int) imageScrollPane.getViewportBounds().getHeight();

        /*photoviewSelection.setImage(
                SwingFXUtils.toFXImage(selectionOverlay, writableSelection));*/

        selectionCanvas.setWidth(overlayWidth);
        selectionCanvas.setHeight(overlayHeight);
        selectionCanvas.getGraphicsContext2D().setStroke(javafx.scene.paint.Paint.valueOf("red"));
    }

    private boolean askForConfirmation(String body) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Waarschuwing!");
        alert.setContentText(body);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    private void resizeImage() {
        if (!checkForSelections()) return;

        filteredImage.resize(imageWidth, imageHeight);
        sizeWarning.setVisible(imageWidth > 4096 || imageHeight > 4096);

        updateImage();
    }

    private void updateImage() {
        filteredImage.redraw();
        updateTexts();
    }

    /* FXML Hooks */
    public void zoomIn() {
        photoView.setFitWidth(Math.floor(photoView.getFitWidth() * 2));
        photoView.setFitHeight(photoView.getFitHeight() * 1.2);
    }

    public void zoomOut() {
        photoView.setFitWidth(Math.floor(photoView.getFitWidth() / 2));
        photoView.setFitHeight(photoView.getFitHeight() / 1.2);
    }

    public void fitWindow(ActionEvent actionEvent) {
        if (image.getHeight() <= image.getWidth())
            photoView.setFitWidth(vboxPhotoView.getWidth() - 2);
        else
            photoView.setFitHeight(vboxPhotoView.getHeight() - 2);
    }

    public void openBindingColorSelector(ActionEvent actionEvent) throws IOException {
        BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        bf.setManualAssign(true);

        CreateWindow newWindow = new CreateWindow("Link kleuren met bindingen", 800.0, 600.0, "components/ColorBindingLinker.fxml", false, false);
        ((ColorBindingLinker) newWindow.getController()).initialize(this.filteredImage);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
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
                .addListener(this.showMarkingChangeListener);
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
                .getComboBoxBindings()
                .addEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);*/

        vboxSelectBinding
                .getComboBoxLevels()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(this.markedColorChangeListener);

        photoView
                .setOnMouseDragged(ManipulatePixel());
        photoView.setOnMousePressed((event) -> {
            double xPercent = event.getX() / photoView.getBoundsInParent().getWidth();
            double yPercent = event.getY() / photoView.getBoundsInParent().getHeight();

            int pX = (int) (writablePhotoview.getWidth() * xPercent);
            int pY = (int) (writablePhotoview.getHeight() * yPercent);

            if (editing) {
                int overlayX = (int) (event.getSceneX() - contentStackpane.localToScene(contentStackpane.getBoundsInLocal()).getMinX());
                int overlayY = (int) (event.getSceneY() - contentStackpane.localToScene(contentStackpane.getBoundsInLocal()).getMinY());

                pXStart = pX;
                pYStart = pY;

                pXStartSelection = overlayX;
                pYStartSelection = overlayY;

                selectionPoints = new ArrayList<>();

                selectionCanvas.getGraphicsContext2D().clearRect(0, 0, selectionCanvas.getWidth(), selectionCanvas.getHeight());
            }

            if (picking) {
                int posterizeLevel = PrimitiveUtil.decomposeIntToBytes(filteredImage.getMetaDataAt(pX, pY))[0];
                BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);

                if (posterizeLevel == vboxSelectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem()) {
                    bf.setMarkedBinding(vboxSelectBinding.getSelectedBinding());
                    checkBoxMarkBinding.setSelected(true);
                } else {
                    checkBoxMarkBinding
                            .selectedProperty()
                            .removeListener(this.showMarkingChangeListener);

                    checkBoxMarkBinding.setSelected(true);

                    checkBoxMarkBinding
                            .selectedProperty()
                            .addListener(this.showMarkingChangeListener);

                    vboxSelectBinding.getComboBoxLevels().getSelectionModel().select((Integer) posterizeLevel);
                }

                togglePicker(null);
            }
        });
        photoView.setOnMouseReleased((event) -> {
            if (!editing) return;

            if (pXPrevious < 0 || pYPrevious < 0) {
                pXPrevious = pXStart;
                pYPrevious = pYStart;
            }

            if (pXPreviousSelection < 0 || pYPreviousSelection < 0) {
                pXPreviousSelection = pXStartSelection;
                pYPreviousSelection = pYStartSelection;
            }

            if (shouldUseCanvasLines()) {
                drawLine(writablePhotoview.getPixelWriter(), pXStart, pYStart, pXPrevious, pYPrevious, false);
                selectionCanvas.getGraphicsContext2D().strokeLine(pXStartSelection, pYStartSelection, pXPreviousSelection, pYPreviousSelection);
            } else {
                drawLine(writablePhotoview.getPixelWriter(), pXStart, pYStart, pXPrevious, pYPrevious, true);
            }


            pXPrevious = -1;
            pYPrevious = -1;

            pXPreviousSelection = -1;
            pYPreviousSelection = -1;

            try {
                showChangeSelectionBindingWindow(new Region(selectionPoints));
            } catch (IOException e) {
                e.printStackTrace();
            }

            selectionCanvas.getGraphicsContext2D().clearRect(0, 0, selectionCanvas.getWidth(), selectionCanvas.getHeight());
        });
    }

    private void updatePosterizationLevelOnImage(MouseEvent mouseEvent) {
        if (askForConfirmation("Als je het posterize-niveau aanpast gaan alle andere wijzigingen verloren, doorgaan?")) {
            posterizeScale = sliderPosterizationScale.valueProperty().intValue();
            labelAmountOfColors.setText("Amount of colors: " + posterizeScale);

            PosterizeFilter posterizeFilter = (PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class);
            posterizeFilter.setLevels(posterizeScale);

            BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
            bf.getBindingsMap().clear();

            updateImage();
        }
    }

    private boolean shouldUseCanvasLines() {
        double zoom;

        if (photoView.getFitWidth() > 0) {
            zoom = photoView.getFitWidth() / photoView.getImage().getWidth();
        } else {
            zoom = photoView.getFitHeight() / photoView.getImage().getWidth();
        }

        return zoom < 5;
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

    public void showMarkingOnImageView() {
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

    private boolean checkForSelections() {
        BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        if (bf.getRegions().size() > 0) {
            return askForConfirmation("De afbeelding bevat selecties! Als je doorgaat, worden deze selecties verwijderd.");
        } else {
            return true;
        }
    }

    private void resizeImageIfOk(Boolean newValue) {
        if (!newValue && (imageHeight != image.getHeight() || imageWidth != image.getWidth())) {
            resizeImage();
        }
    }

    private void ResizeImageWidth(Observable observable, Boolean oldValue, Boolean newValue) {
        resizeImageIfOk(newValue);
    }

    private void ResizeImageHeight(Observable observable, Boolean oldValue, Boolean newValue) {
        resizeImageIfOk(newValue);
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

                    this.vboxSelectBinding.setBindingsMap(((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class)).getBindingsMap(),
                            (((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class))));


                    vboxSelectBinding
                            .getComboBoxLevels()
                            .getSelectionModel()
                            .selectedItemProperty()
                            .addListener(this.markedColorChangeListener);
                    vboxSelectBinding.addEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);

                    redrawPhotoView();
                    repopulateSelectionList();
                });
    }


    public void ShowCalculateWindow(ActionEvent actionEvent) throws IOException {
        if (!checkForSelections()) return;

        CreateWindow newWindow = new CreateWindow("Afplatting berekenen", 0.0, 0.0, "view/CalculateFlattening.fxml", false, true);
        ((CalculateFlattening) newWindow.getController()).initialize(this.filteredImage);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);

        textFieldWidth.textProperty().setValue(String.valueOf(filteredImage.getModifiedImage().getWidth()));
        textFieldHeight.textProperty().setValue(String.valueOf(filteredImage.getModifiedImage().getHeight()));

        filteredImage.redraw();
    }

    public void showChangeSelectionBindingWindow(Region region) throws IOException {

        CreateWindow newWindow = new CreateWindow("Verander specifieke binding in selectie", 0, 0, "view/ChangeSelectionBinding.fxml", false, true);
        ((ChangeSelectionBinding) newWindow.getController()).initialize(this.filteredImage, region);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
        newWindow.getStage().setOnCloseRequest(((ChangeSelectionBinding) newWindow.getController()).getCloseEventHandler());

        region.setMarked(false);

        filteredImage.redraw();
    }


    private void drawLine(PixelWriter pw, int x1, int y1, int x2, int y2, boolean draw) {
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

                if (!selectionPoints.contains(new Point(x, y))) {
                    if (draw) pw.setColor(x, y, javafx.scene.paint.Color.RED);
                    selectionPoints.add(new Point(x, y));
                }

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
                if (!selectionPoints.contains(new Point(x, y))) {
                    if (draw) pw.setColor(x, y, javafx.scene.paint.Color.RED);
                    selectionPoints.add(new Point(x, y));
                }

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

            double overlayX = event.getSceneX() - contentStackpane.localToScene(contentStackpane.getBoundsInLocal()).getMinX();
            double overlayY = event.getSceneY() - contentStackpane.localToScene(contentStackpane.getBoundsInLocal()).getMinY();

            double xPercent = event.getX() / photoView.getBoundsInParent().getWidth();
            double yPercent = event.getY() / photoView.getBoundsInParent().getHeight();

            int pX = (int) (writablePhotoview.getWidth() * xPercent);
            int pY = (int) (writablePhotoview.getHeight() * yPercent);

            pX = Math.min(Math.max(0, pX), (int) writablePhotoview.getWidth() - 1);
            pY = Math.min(Math.max(0, pY), (int) writablePhotoview.getHeight() - 1);

            overlayX = Math.min(Math.max(0, overlayX), (int) selectionCanvas.getWidth() - 1);
            overlayY = Math.min(Math.max(0, overlayY), (int) selectionCanvas.getHeight() - 1);

            if (pXPrevious == -1 || pYPrevious == -1) {
                if (!shouldUseCanvasLines()) {
                    writablePhotoview.getPixelWriter().setColor(pX, pY, javafx.scene.paint.Color.RED);
                }
                selectionPoints.add(new Point(pX, pY));
            } else {
                drawLine(writablePhotoview.getPixelWriter(), pXPrevious, pYPrevious, pX, pY, !shouldUseCanvasLines());
            }

            if (pXPreviousSelection == -1 || pYPreviousSelection == -1) {
                if (shouldUseCanvasLines())
                    selectionCanvas.getGraphicsContext2D().strokeLine(overlayX, overlayY, overlayX, overlayY);
            } else {
                selectionCanvas.getGraphicsContext2D().setLineWidth(2);
                if (shouldUseCanvasLines())
                    selectionCanvas.getGraphicsContext2D().strokeLine(pXPreviousSelection, pYPreviousSelection, overlayX, overlayY);
            }

            pXPrevious = pX;
            pYPrevious = pY;

            pXPreviousSelection = overlayX;
            pYPreviousSelection = overlayY;
        };
    }

    public void toggleEdit(ActionEvent actionEvent) {
        if (picking) togglePicker(null);

        editing = !editing;

        updateModeVisual(editing, toggleEditButton);
    }

    public void togglePicker(ActionEvent actionEvent) {
        if (editing) toggleEdit(null);

        picking = !picking;

        /*if (picking) {
            checkBoxMarkBinding.setSelected(true);
            //MarkColorOnImageView(null);
        }*/

        updateModeVisual(picking, togglePickerButton);
    }

    private void updateModeVisual(boolean flag, JFXButton button) {
        if (flag) {
            button.setStyle("-fx-background-color: -app-color-secondary;");
            imageScrollPane.setPannable(false);
        } else {
            button.setStyle("");
            imageScrollPane.setPannable(true);
        }
    }

    public void showAbout(ActionEvent actionEvent) throws IOException {
        CreateWindow newWindow = new CreateWindow("About", 0.0, 0.0, "view/About.fxml", false, true);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
    }

    public void makeNewFile(ActionEvent actionEvent) throws IOException {
        CreateFilePicker fp = new CreateFilePicker(imageProperties.loadTitle, this.stage, imageProperties.filterDescription, imageProperties.filterExtensions);
        File file = fp.getFile();

        if (file != null) {
            CreateWindow newWindow = new CreateWindow("Verilin | PhotoWeave", 800.0, 600.0, "view/EditPhoto.fxml", false, true);
            ((EditPhoto) newWindow.getController()).initialize(file.getAbsolutePath());
            newWindow.showWindow();
        }
    }

    public void openFile(ActionEvent actionEvent) throws IOException {
        CreateFilePicker fp = new CreateFilePicker(jsonProperties.loadTitle, this.stage, jsonProperties.filterDescription, jsonProperties.filterExtensions);
        File file = fp.getFile();

        if (file != null) {
            CreateWindow newWindow = new CreateWindow("Verilin | PhotoWeave", 800.0, 600.0, "view/EditPhoto.fxml", false, true);
            ((EditPhoto) newWindow.getController()).initialize(file.getAbsolutePath());
            newWindow.showWindow();
        }
    }

    public void saveFile(ActionEvent actionEvent) {
        openSaveWindow(filterDescription.JSON);
    }

    public void exportImage(ActionEvent actionEvent) {
        openSaveWindow(filterDescription.BITMAP);
    }

    public void closeWindow(ActionEvent actionEvent) throws IOException {
        openSaveWarningWindow(filterDescription.JSON, true, false);
    }

    private void openSaveWindow(filterDescription filterDescription) {
        CreateFilePicker fp;
        if (filterDescription == filterDescription.BITMAP) {
            fp = new CreateFilePicker(bitmapProperties.title, this.stage, bitmapProperties.filterDescription, bitmapProperties.filterExtensions);
        } else if (filterDescription == filterDescription.JSON) {
            fp = new CreateFilePicker(jsonProperties.saveTitle, this.stage, jsonProperties.filterDescription, jsonProperties.filterExtensions);
        } else {
            fp = new CreateFilePicker(allFilesProperties.saveTitle, this.stage, allFilesProperties.filterDescription, allFilesProperties.filterExtensions);
        }

        File file = fp.saveFile();

        if (file != null) {
            try {
                if (filterDescription.equals("Bitmap")) {
                    ImageIO.write(ImageUtil.convertImageToByteBinary(filteredImage.getModifiedImage()), "bmp", file);
                } else if (filterDescription.equals("JSON File")) {

                } else {

                }
            } catch (IOException ex) {
            }
        }
    }

    private void openStartWindow() throws IOException {
        CreateWindow newWindow = new CreateWindow("Verilin | PhotoWeave", 0, 0, "view/OpenPhoto.fxml", false, true);
        ((OpenPhoto) newWindow.getController()).initialize();
        newWindow.showWindow();
    }

    private void openSaveWarningWindow(filterDescription filterDescription, boolean closeWindow, boolean newWindow) throws IOException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Waarschuwing!");
        alert.setHeaderText("U zal de huidige pagina verlaten. Wijzigingen aan uw bestand kunnen mogelijks verloren raken. Wilt u uw bestand eerst opslaan?");
        alert.setContentText("Druk op opslaan om uw bestand op te slaan, negeren om uw wijzigingen te annuleren en annuleren om verder te gaan met uw huidige bestand.");

        ButtonType buttonSave = new ButtonType("Opslaan");
        ButtonType buttonIgnore = new ButtonType("Negeren");
        ButtonType buttonCancel = new ButtonType("Annuleer", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonSave, buttonIgnore, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonSave) {
            openSaveWindow(filterDescription);
            whatWithWindow(closeWindow, newWindow);
        } else if (result.get() == buttonIgnore) {
            whatWithWindow(closeWindow, newWindow);
        } else if (result.get() == buttonCancel) {
            alert.close();
        }
    }

    private void whatWithWindow(boolean closeWindow, boolean newWindow) throws IOException {
        if (closeWindow) {
            this.stage.close();
        }
        if (newWindow) {
            openStartWindow();
        }
    }

    private void repopulateSelectionList() {

        //Delete regions that no longer exist
        BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);

        Iterator<Region> selectionListIterator = selectionsList.getItems().iterator();
        ArrayList<Region> itemsToRemove = new ArrayList<>();

        while (selectionListIterator.hasNext()) {
            Region region = selectionListIterator.next();

            if (!bf.getRegions().contains(region)) {
                itemsToRemove.add(region);
            }
        }

        //Add regions that don't exist yet
        Iterator<Region> bindingRegionsIterator = bf.getRegions().iterator();
        ArrayList<Region> itemsToAdd = new ArrayList<>();

        while (bindingRegionsIterator.hasNext()) {
            Region region = bindingRegionsIterator.next();

            if (!selectionsList.getItems().contains(region)) {
                itemsToAdd.add(region);
            }
        }

        selectionsList.getItems().removeAll(itemsToRemove);
        selectionsList.getItems().addAll(itemsToAdd);
    }
}