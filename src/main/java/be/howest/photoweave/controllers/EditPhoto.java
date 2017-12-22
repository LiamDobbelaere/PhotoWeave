package be.howest.photoweave.controllers;

import be.howest.photoweave.components.*;
import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.components.events.BindingChangedEventHandler;
import be.howest.photoweave.model.ParametersInterface;
import be.howest.photoweave.model.customFile.LoadFilteredImageController;
import be.howest.photoweave.model.customFile.SaveFilteredImageController;
import be.howest.photoweave.model.customFile.data.UserInterfaceData;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.PosterizeFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.util.ImageUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class EditPhoto implements ParametersInterface {
    /* FXML User Interface */
    public AnchorPane anchorPaneWindow, anchorPanePhotoView;
    public TitledPane paneDefault;
    public ScrollPane imageScrollPane;
    public StackPane contentStackpane;
    public VBox vboxPhotoView;
    public Accordion properties;
    public Canvas selectionCanvas;

    public Label labelFileNameId, labelAmountOfColors, sizeWarning, filePath;

    public Slider sliderPosterizationScale;
    public TextField textFieldWidth, textFieldHeight;

    public PixelatedImageView photoView;
    public SelectBinding vboxSelectBinding;

    public JFXCheckBox checkBoxMarkBinding, checkBoxInvert, checkBoxFloaters;
    public JFXTextField textFieldXFloaters, textFieldYFloaters;
    public JFXButton toggleEditButton, togglePickerButton;

    /* User Interface Data */
    public int imageWidth, imageHeight, posterizeScale;
    public String filename;
    public BufferedImage image;
    public FilteredImage filteredImage;
    public Stage stage;

    public UserInterfaceData userInterfaceData;
    public boolean isCustomFile;
    /* Handlers hier nodig?*/

    public BindingChangedEventHandler bindingChangedEventHandler;
    public ChangeListener<Integer> markedColorChangeListener;
    public ChangeListener<Boolean> showMarkingChangeListener;

    /* SELECTION */

    public boolean editing = false;
    public boolean picking = false;

    public WritableImage writablePhotoview;

    public double XScroll;
    public double YScroll;

    public boolean onLoad;
    private EditPhotoEventHandlers editPhotoEventHandlers;

    public double getXScroll() {
        return XScroll;
    }

    public double getYScroll() {
        return YScroll;
    }

    public void initialize(String path) throws IOException {
        this.onLoad = true;
        this.posterizeScale = 10;
        LoadFilteredImageController lfic;
        if (FilenameUtils.getExtension(path).toLowerCase().equals("json")) {
            isCustomFile = true;
            lfic = new LoadFilteredImageController(new File(path), this);
            this.image = lfic.getFilteredImage().getOriginalImage();
        } else {
            isCustomFile = false;
            this.image = ImageIO.read(new File(path));
            lfic = new LoadFilteredImageController(this.image, this.posterizeScale, false, 0, 0, this);
        }

        this.filteredImage = lfic.getFilteredImage();

        // UI
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.filename = path.substring(path.lastIndexOf("/") + 1);

        // Global
        this.stage = (Stage) anchorPaneWindow.getScene().getWindow();

        //if (isCustomFile) lfic.loadDataBeforeListenAreHooked();

        this.editPhotoEventHandlers = new EditPhotoEventHandlers(this);
        this.editPhotoEventHandlers.initializeListeners();

        initializePhotoScale();
        if (isCustomFile) lfic.loadDataInUserInterface();
        updateUserInterfaceText();
        updateBindingSelection();

        paneDefault.setExpanded(true);
    }

    public void fuckingWerk() {
        imageScrollPane.layout();
        anchorPaneWindow.layout();

        imageScrollPane.setVvalue(getYScroll());
        imageScrollPane.setHvalue(getXScroll());

        System.out.println(imageScrollPane.getVvalue());
        System.out.println(imageScrollPane.getHvalue());
        System.out.println(imageScrollPane.getWidth());
        System.out.println(imageScrollPane.getHeight());
    }

    /* UI */
    private void initializePhotoScale() {
        if (image.getHeight() <= image.getWidth())
            photoView.setFitWidth(vboxPhotoView.getWidth() - 2);
        else
            photoView.setFitHeight(vboxPhotoView.getHeight() - 2);

        updateImage();
    }


    void updateImage() {
        filteredImage.redraw();
        updateUserInterfaceText();
    }

    private void updateUserInterfaceText() {
        labelFileNameId.setText(new File(filename).getName()); //TODO keeps making a new file object. Only do this in Init <- make extra method for this so it is called ones
        filePath.setText(filename);
        textFieldWidth.setText(String.valueOf(this.filteredImage.getModifiedImage().getWidth()));
        textFieldHeight.setText(String.valueOf(this.filteredImage.getModifiedImage().getHeight()));
        labelAmountOfColors.setText("Aantal tinten: " + ((PosterizeFilter) filteredImage.getFilters().findRGBFilter(PosterizeFilter.class)).getLevelCount());
    }

    private void updateBindingSelection() {
        BindingFilter bf = ((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class));
        this.vboxSelectBinding.setBindingsMap(bf.getBindingsMap(), bf);
    }

    private void redrawPhotoView() {
        writablePhotoview = SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null);
        photoView.setImage(writablePhotoview);

        if (onLoad) {
            fuckingWerk();
            onLoad = false;
        }

        int overlayWidth = (int) imageScrollPane.getViewportBounds().getWidth();
        int overlayHeight = (int) imageScrollPane.getViewportBounds().getHeight();

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


    /* FXML Hooks */
    public void zoomIn() {
        photoView.setFitWidth(Math.floor(photoView.getFitWidth() * 2));
        photoView.setFitHeight(photoView.getFitHeight() * 2);
        System.out.println(imageScrollPane.getHvalue());
        System.out.println(imageScrollPane.getVvalue());
    }

    public void zoomOut() {
        photoView.setFitWidth(Math.floor(photoView.getFitWidth() / 2));
        photoView.setFitHeight(photoView.getFitHeight() / 2);
    }

    public void fitWindow(ActionEvent actionEvent) {
        if (image.getHeight() <= image.getWidth())
            photoView.setFitWidth(vboxPhotoView.getWidth() - 2);
        else
            photoView.setFitHeight(vboxPhotoView.getHeight() - 2);
    }

    public void saveImage(ActionEvent actionEvent) {
        CreateFilePicker fp = new CreateFilePicker("PhotoWeave | Save Image", "user.home", this.stage, "Bitmap", ".bmp");

        File file = fp.saveFile();

        if (file != null) {
            try {
                ImageIO.write(ImageUtil.convertImageToByteBinary(filteredImage.getModifiedImage()), "bmp", file);
            } catch (IOException ex) {
            }
        }
    }

    public void openBindingCreator(ActionEvent actionEvent) throws IOException {
        CreateWindow newWindow = new CreateWindow("PhotoWeave | Maak Binding", 800.0, 600.0, "components/BindingMaker.fxml", false, false);
        ((BindingMaker) newWindow.getController()).initialize();
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
        System.out.println("BINDCREATOR");
        System.out.println(getXScroll());
        System.out.println(getYScroll());
        imageScrollPane.setVvalue(getYScroll());
        imageScrollPane.setHvalue(getXScroll());
        System.out.println(imageScrollPane.getHvalue());
        System.out.println(imageScrollPane.getVvalue());
    }

    public void openBindingColorSelector(ActionEvent actionEvent) throws IOException {
        BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        bf.setManualAssign(true);

        CreateWindow newWindow = new CreateWindow("PhotoWeave | Link Kleuren met Bindingen", 800.0, 600.0, "components/ColorBindingLinker.fxml", false, true);
        ((ColorBindingLinker) newWindow.getController()).initialize(this.filteredImage);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
    }

    /* INTERFACE
     * ThreadListener
     */
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
        //fuckingWerk();
        //redrawPhotoView(); //Optional, but shows the thread's progress in realtime
    }

    @Override
    public void onRedrawComplete() {
        Platform.runLater(
                () -> {

                    updateBindingSelection();

                    vboxSelectBinding
                            .getComboBoxLevels()
                            .getSelectionModel()
                            .selectedItemProperty()
                            .addListener(this.markedColorChangeListener);
                    vboxSelectBinding.addEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);

                    redrawPhotoView();
                });
    }


    public void ShowCalculateWindow(ActionEvent actionEvent) throws IOException {
        if (!checkForSelections()) return;

        CreateWindow newWindow = new CreateWindow("Afplatting Berekenen", 0.0, 0.0, "view/CalculateFlattening.fxml", false, true);
        ((CalculateFlattening) newWindow.getController()).initialize(this.filteredImage);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);

        textFieldWidth.textProperty().setValue(String.valueOf(filteredImage.getModifiedImage().getWidth()));
        textFieldHeight.textProperty().setValue(String.valueOf(filteredImage.getModifiedImage().getHeight()));

        filteredImage.redraw();
    }

    boolean checkForSelections() {
        BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);
        if (bf.getRegions().size() > 0) {
            return askForConfirmation("De afbeelding bevat selecties! Als je doorgaat, worden deze selecties verwijderd.");
        } else {
            return true;
        }
    }


    public void showChangeSelectionBindingWindow(Region region) throws IOException {
        CreateWindow newWindow = new CreateWindow("Verander specifieke binding in selectie", 0, 0, "view/ChangeSelectionBinding.fxml", false, true);
        ((ChangeSelectionBinding) newWindow.getController()).initialize(this.filteredImage, region);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
        newWindow.getStage().setOnCloseRequest(((ChangeSelectionBinding) newWindow.getController()).getCloseEventHandler());

        region.setMarked(false);

        filteredImage.redraw();
    }


    /* FXML Hook */
    public void toggleEdit(ActionEvent actionEvent) {
        if (picking) togglePicker(null);

        editing = !editing;

        updateModeVisual(editing, toggleEditButton);
    }

    /* FXML Hook */
    public void togglePicker(ActionEvent actionEvent) {
        try {
            collectUserInterfaceData();
            new SaveFilteredImageController(this.filteredImage, this.userInterfaceData).save("C:\\Users\\Quinten\\Pictures\\verilin\\SavedFile\\verilin.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (editing) toggleEdit(null);

        picking = !picking;

        /*if (picking) {
            checkBoxMarkBinding.setSelected(true);
            //MarkColorOnImageView(null);
        }*/

        updateModeVisual(picking, togglePickerButton);
    }

    /* Enchantment */
    private void updateModeVisual(boolean flag, JFXButton button) {
        if (flag) {
            button.setStyle("-fx-background-color: -app-color-secondary;");
            imageScrollPane.setPannable(false);
        } else {
            button.setStyle("");
            imageScrollPane.setPannable(true);
        }
    }

    /* INTERFACE
     * ParametersInterface
     */
    @Override
    public void setUIComponentInverted(boolean asBoolean) {

        checkBoxInvert.selectedProperty().setValue(asBoolean);
    }

    @Override
    public void setUIComponentMarked(boolean asBoolean) {
        checkBoxMarkBinding.selectedProperty().setValue(asBoolean);
    }

    @Override
    public void setUICompentenPosterize(int asInt) {
        sliderPosterizationScale.setValue(asInt);
    }

    @Override
    public void setUIComponentViewHeight(double asDouble) {
        System.out.println("== fit height ==");
        System.out.println(asDouble);
        System.out.println(photoView.getFitHeight());

        this.photoView.setFitHeight(asDouble);

        System.out.println(photoView.getFitHeight());
    }

    @Override
    public void setUIComponentViewWidth(double asDouble) {
        System.out.println("== fit width ==");
        System.out.println(asDouble);
        System.out.println(photoView.getFitWidth());

        this.photoView.setFitWidth(asDouble);

        System.out.println(photoView.getFitWidth());

    }

    @Override
    public void setUIComponentXScroll(double asDouble) {
        //System.out.println("SCROLL X CALLED");
        //imageScrollPane.layout();
        //System.out.println(asDouble);
        //imageScrollPane.layout();
        //imageScrollPane.setHvalue(asDouble);
        this.XScroll = asDouble;
    }

    @Override
    public void setUIComponentYScroll(double asDouble) {
        //imageScrollPane.layout();
        //imageScrollPane.setVvalue(asDouble);
        this.YScroll = asDouble;
    }

    @Override
    public void setUIComponentXFloater(int asInt) {
        this.textFieldXFloaters.setText(String.valueOf(asInt));
    }

    @Override
    public void setUIComponentYFloater(int asInt) {
        this.textFieldYFloaters.setText(String.valueOf(asInt));
    }

    @Override
    public void setUIComponentWidth(int width) {
        this.textFieldWidth.setText(String.valueOf(width));
    }

    @Override
    public void setUIComponentHeight(int height) {
        this.textFieldWidth.setText(String.valueOf(height));
    }


    /* FXML Hook add button*/
    private void collectUserInterfaceData() {
        this.userInterfaceData = new UserInterfaceData();
        this.userInterfaceData.setInverted(this.checkBoxInvert.selectedProperty().getValue());
        //this.userInterfaceData.setMarked(this.checkBoxMarkBinding.selectedProperty().getValue());
        //this.userInterfaceData.setBindingIndex(0); //TODO change
        this.userInterfaceData.setxFloater(Integer.parseInt(this.textFieldXFloaters.getText()));
        this.userInterfaceData.setyFloater(Integer.parseInt(this.textFieldYFloaters.getText()));
        this.userInterfaceData.setViewHeight(this.photoView.getFitHeight());
        this.userInterfaceData.setViewWidth(this.photoView.getFitWidth());
        this.userInterfaceData.setxScroll(this.imageScrollPane.getHvalue());
        this.userInterfaceData.setyScroll(this.imageScrollPane.getVvalue());

    }

    public void showAbout(ActionEvent actionEvent) throws IOException {
        CreateWindow newWindow = new CreateWindow("About", 0.0, 0.0, "view/About.fxml", false, true);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
    }


    public ScrollPane getImageScrollPane() {
        return imageScrollPane;
    }
}