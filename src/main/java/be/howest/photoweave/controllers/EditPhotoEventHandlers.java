package be.howest.photoweave.controllers;

import be.howest.photoweave.components.events.BindingChangedEventHandler;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.imagefilters.FloatersFilter;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.PosterizeFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.PrimitiveUtil;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditPhotoEventHandlers {
    private EditPhoto parent;

    public int pXStart = -1;
    public int pYStart = -1;
    public int pXPrevious = -1;
    public int pYPrevious = -1;

    public double pXStartSelection = -1;
    public double pYStartSelection = -1;
    public double pXPreviousSelection = -1;
    public double pYPreviousSelection = -1;

    public List<Point> selectionPoints;


    public EditPhotoEventHandlers(EditPhoto parent){
        this.parent = parent;

                /* DIT NAAR INITLIST */
        parent.bindingChangedEventHandler = new BindingChangedEventHandler() {
            @Override
            public void onBindingChanged() {
                parent.filteredImage.redraw();
            }
        };

        parent.markedColorChangeListener = (observable, oldValue, newValue) -> {
            this.MarkColorOnImageView(observable);
        };

        parent.showMarkingChangeListener = (observable, oldValue, newValue) -> {
            showMarkingOnImageView();
        };
        /* ------------ */
    }

    /* Event Handlers */
    public void initializeListeners() {
        /* User Interface */

        parent.sliderPosterizationScale
                .setOnMouseReleased(this::updatePosterizationLevelOnImage);
        parent.textFieldWidth
                .focusedProperty()
                .addListener(this::ResizeImageHeight);
        parent.textFieldHeight
                .focusedProperty()
                .addListener(this::ResizeImageWidth);
        parent.textFieldHeight
                .textProperty()
                .addListener(this::ChangeImageHeight);
        parent.textFieldWidth
                .textProperty()
                .addListener(this::ChangeImageWidth);
        parent.checkBoxMarkBinding
                .selectedProperty()
                .addListener(parent.showMarkingChangeListener);
        parent.checkBoxInvert
                .selectedProperty()
                .addListener(this::InvertColorsInWovenImage);
        parent.checkBoxFloaters
                .selectedProperty()
                .addListener(this::ShowFloatersOnImageView);
        parent.textFieldXFloaters
                .textProperty()
                .addListener(this::ChangeXFloatersThreshold);
        parent.textFieldYFloaters
                .textProperty()
                .addListener(this::ChangeYFloatersThreshold);

        parent.anchorPaneWindow
                .heightProperty()
                .addListener(this::ResizeImageViewHeight);
        parent.anchorPaneWindow
                .widthProperty()
                .addListener(this::ResizeImageViewWidth);

        parent.vboxSelectBinding
                .getComboBoxLevels()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(parent.markedColorChangeListener);

        /* Selection Feature */
        parent.photoView
                .setOnMouseDragged(ManipulatePixel());

        parent.photoView.setOnMousePressed((event) -> {
            double xPercent = event.getX() / parent.photoView.getBoundsInParent().getWidth();
            double yPercent = event.getY() / parent.photoView.getBoundsInParent().getHeight();

            int pX = (int) (parent.writablePhotoview.getWidth() * xPercent);
            int pY = (int) (parent.writablePhotoview.getHeight() * yPercent);

            if (parent.editing) {
                int overlayX = (int) (event.getSceneX() - parent.contentStackpane.localToScene(parent.contentStackpane.getBoundsInLocal()).getMinX());
                int overlayY = (int) (event.getSceneY() - parent.contentStackpane.localToScene(parent.contentStackpane.getBoundsInLocal()).getMinY());

                pXStart = pX;
                pYStart = pY;

                pXStartSelection = overlayX;
                pYStartSelection = overlayY;

                selectionPoints = new ArrayList<>();

                parent.selectionCanvas.getGraphicsContext2D().clearRect(0, 0, parent.selectionCanvas.getWidth(), parent.selectionCanvas.getHeight());
            }

            if (parent.picking) {
                int posterizeLevel = PrimitiveUtil.decomposeIntToBytes(parent.filteredImage.getMetaDataAt(pX, pY))[0];
                BindingFilter bf = (BindingFilter) parent.filteredImage.getFilters().findRGBFilter(BindingFilter.class);

                if (posterizeLevel == parent.vboxSelectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem()) {
                    bf.setMarkedBinding(parent.vboxSelectBinding.getSelectedBinding());
                    parent.checkBoxMarkBinding.setSelected(true);
                } else {
                    parent.checkBoxMarkBinding
                            .selectedProperty()
                            .removeListener(parent.showMarkingChangeListener);

                    parent.checkBoxMarkBinding.setSelected(true);

                    parent.checkBoxMarkBinding
                            .selectedProperty()
                            .addListener(parent.showMarkingChangeListener);

                    parent.vboxSelectBinding.getComboBoxLevels().getSelectionModel().select((Integer) posterizeLevel);
                }

                parent.togglePicker(null);
            }
        });
        parent.photoView.setOnMouseReleased((event) -> {
            if (!parent.editing) return;

            if (pXPrevious < 0 || pYPrevious < 0) {
                pXPrevious = pXStart;
                pYPrevious = pYStart;
            }

            if (pXPreviousSelection < 0 || pYPreviousSelection < 0) {
                pXPreviousSelection = pXStartSelection;
                pYPreviousSelection = pYStartSelection;
            }

            if (shouldUseCanvasLines()) {
                drawLine(parent.writablePhotoview.getPixelWriter(), pXStart, pYStart, pXPrevious, pYPrevious, false);
                parent.selectionCanvas.getGraphicsContext2D().strokeLine(pXStartSelection, pYStartSelection, pXPreviousSelection, pYPreviousSelection);
            } else {
                drawLine(parent.writablePhotoview.getPixelWriter(), pXStart, pYStart, pXPrevious, pYPrevious, true);
            }


            pXPrevious = -1;
            pYPrevious = -1;

            pXPreviousSelection = -1;
            pYPreviousSelection = -1;

            try {
                parent.showChangeSelectionBindingWindow(new Region(selectionPoints));
            } catch (IOException e) {
                e.printStackTrace();
            }

            parent.selectionCanvas.getGraphicsContext2D().clearRect(0, 0, parent.selectionCanvas.getWidth(), parent.selectionCanvas.getHeight());
        });


    }

    private void updatePosterizationLevelOnImage(MouseEvent mouseEvent) {
        if (askForConfirmation("Als je het posterize-niveau aanpast gaan alle andere wijzigingen verloren, doorgaan?")) {
            parent.posterizeScale = parent.sliderPosterizationScale.valueProperty().intValue();
            parent.labelAmountOfColors.setText("Amount of colors: " + parent.posterizeScale);

            PosterizeFilter posterizeFilter = (PosterizeFilter) parent.filteredImage.getFilters().findRGBFilter(PosterizeFilter.class);
            posterizeFilter.setLevels(parent.posterizeScale);

            BindingFilter bf = (BindingFilter) parent.filteredImage.getFilters().findRGBFilter(BindingFilter.class);
            bf.getBindingsMap().clear();
            bf.getRegions().clear();

            parent.updateImage();
        }
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

    private void ResizeImageViewHeight(Observable observable, Number oldValue, Number newValue) {
        parent.vboxPhotoView.setMinHeight((Double) newValue - 50);
    }

    private void ResizeImageViewWidth(Observable observable, Number oldValue, Number newValue) {
        parent.vboxPhotoView.setMinWidth((Double) newValue - 200);
    }

    private void ChangeImageWidth(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            parent.textFieldWidth.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!parent.textFieldWidth.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                parent.imageWidth = Integer.parseInt(newValue);
            }
        }

    }

    private void ChangeImageHeight(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            parent.textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!parent.textFieldHeight.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                parent.imageHeight = Integer.parseInt(newValue);
            }
        }

    }

    public void showMarkingOnImageView() {
        BindingFilter bindingFilter = (BindingFilter) parent.filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        Binding selectedBinding = bindingFilter.getBindingsMap().get(parent.vboxSelectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem());

        if (selectedBinding == null)
            selectedBinding = bindingFilter.getBindingsMap().get(parent.vboxSelectBinding.getComboBoxLevels().getItems().get(0));

        bindingFilter.setMarkedBinding(selectedBinding);
        bindingFilter.setShowMarkedBinding(parent.checkBoxMarkBinding.isSelected());
        parent.filteredImage.redraw();
    }

    private void InvertColorsInWovenImage(Observable observable, Boolean oldValue, Boolean newValue) {
        BindingFilter bindingFilter = (BindingFilter) parent.filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        bindingFilter.setInverted(newValue);
        parent.filteredImage.redraw();
    }

    private void ShowFloatersOnImageView(Observable observable, Boolean oldValue, Boolean newValue) {
        FloatersFilter floatersFilter =
                (FloatersFilter) parent.filteredImage.getFilters().findImageFilter(FloatersFilter.class);

        floatersFilter.setEnabled(newValue);

        parent.filteredImage.redraw();
    }

    private void ChangeXFloatersThreshold(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            parent.textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!parent.textFieldXFloaters.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                FloatersFilter floatersFilter =
                        (FloatersFilter) parent.filteredImage.getFilters().findImageFilter(FloatersFilter.class);

                floatersFilter.setFloaterTresholdX(Integer.parseInt(newValue));

                parent.filteredImage.redraw();
            }
        }
    }

    private void ChangeYFloatersThreshold(Observable observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            parent.textFieldHeight.setText(newValue.replaceAll("\\D", ""));
        } else {
            if (!parent.textFieldYFloaters.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                FloatersFilter floatersFilter =
                        (FloatersFilter) parent.filteredImage.getFilters().findImageFilter(FloatersFilter.class);

                floatersFilter.setFloaterTresholdY(Integer.parseInt(newValue));

                parent.filteredImage.redraw();
            }
        }
    }

    private void resizeImageIfOk(Boolean newValue) {
        if (!newValue && (parent.imageHeight != parent.image.getHeight() || parent.imageWidth != parent.image.getWidth())) {
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
        return parent.bindingChangedEventHandler;
    }

    private void MarkColorOnImageView(Observable observable) {
        BindingFilter bindingFilter = (BindingFilter) parent.filteredImage.getFilters().findRGBFilter(BindingFilter.class);

        bindingFilter.setMarkedBinding(bindingFilter.getBindingsMap().get(
                parent.vboxSelectBinding.getComboBoxLevels().getSelectionModel().getSelectedItem()));
        bindingFilter.setShowMarkedBinding(parent.checkBoxMarkBinding.isSelected());
        parent.filteredImage.redraw();
    }





    private void resizeImage() {
        if (!parent.checkForSelections()) return;

        parent.filteredImage.resize(parent.imageWidth, parent.imageHeight);
        parent.sizeWarning.setVisible(parent.imageWidth > 4096 || parent.imageHeight > 4096);

        parent.updateImage();
    }



    /* FEATURE
 * SelectionTool
 */
    public void drawLine(PixelWriter pw, int x1, int y1, int x2, int y2, boolean draw) {
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

    public EventHandler<MouseEvent> ManipulatePixel() {
        return event -> {
            if (!parent.editing) return;

            double overlayX = event.getSceneX() - parent.contentStackpane.localToScene(parent.contentStackpane.getBoundsInLocal()).getMinX();
            double overlayY = event.getSceneY() - parent.contentStackpane.localToScene(parent.contentStackpane.getBoundsInLocal()).getMinY();

            double xPercent = event.getX() / parent.photoView.getBoundsInParent().getWidth();
            double yPercent = event.getY() / parent.photoView.getBoundsInParent().getHeight();

            int pX = (int) (parent.writablePhotoview.getWidth() * xPercent);
            int pY = (int) (parent.writablePhotoview.getHeight() * yPercent);

            pX = Math.min(Math.max(0, pX), (int) parent.writablePhotoview.getWidth() - 1);
            pY = Math.min(Math.max(0, pY), (int) parent.writablePhotoview.getHeight() - 1);

            overlayX = Math.min(Math.max(0, overlayX), (int) parent.selectionCanvas.getWidth() - 1);
            overlayY = Math.min(Math.max(0, overlayY), (int) parent.selectionCanvas.getHeight() - 1);

            if (pXPrevious == -1 || pYPrevious == -1) {
                if (!shouldUseCanvasLines()) {
                    parent.writablePhotoview.getPixelWriter().setColor(pX, pY, javafx.scene.paint.Color.RED);
                }
                selectionPoints.add(new Point(pX, pY));
            } else {
                drawLine(parent.writablePhotoview.getPixelWriter(), pXPrevious, pYPrevious, pX, pY, !shouldUseCanvasLines());
            }

            if (pXPreviousSelection == -1 || pYPreviousSelection == -1) {
                if (shouldUseCanvasLines())
                    parent.selectionCanvas.getGraphicsContext2D().strokeLine(overlayX, overlayY, overlayX, overlayY);
            } else {
                parent.selectionCanvas.getGraphicsContext2D().setLineWidth(2);
                if (shouldUseCanvasLines())
                    parent.selectionCanvas.getGraphicsContext2D().strokeLine(pXPreviousSelection, pYPreviousSelection, overlayX, overlayY);
            }

            pXPrevious = pX;
            pYPrevious = pY;

            pXPreviousSelection = overlayX;
            pYPreviousSelection = overlayY;
        };
    }

    boolean shouldUseCanvasLines() {
        double zoom;

        if (parent.photoView.getFitWidth() > 0) {
            zoom = parent.photoView.getFitWidth() / parent.photoView.getImage().getWidth();
        } else {
            zoom = parent.photoView.getFitHeight() / parent.photoView.getImage().getWidth();
        }

        return zoom < 5;
    }


    public void setWritablePhotoview(WritableImage writablePhotoview) {
        //this.writablePhotoview = writablePhotoview;
    }
}
