package be.howest.photoweave.controllers;

import be.howest.photoweave.components.ColorBindingLinker;
import be.howest.photoweave.components.PixelatedImageView;
import be.howest.photoweave.components.SelectBinding;
import be.howest.photoweave.components.SelectionListCell;
import be.howest.photoweave.components.events.BindingChanged;
import be.howest.photoweave.components.events.BindingChangedEventHandler;
import be.howest.photoweave.model.ParametersInterface;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.customFile.LoadFilteredImageController;
import be.howest.photoweave.model.customFile.SaveFilteredImageController;
import be.howest.photoweave.model.customFile.data.UserInterfaceData;
import be.howest.photoweave.model.imaging.FilteredImage;
import be.howest.photoweave.model.imaging.rgbfilters.BindingFilter;
import be.howest.photoweave.model.imaging.rgbfilters.bindingfilter.Region;
import be.howest.photoweave.model.properties.*;
import be.howest.photoweave.model.util.*;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
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
import java.util.ArrayList;
import java.util.Iterator;
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

    public JFXListView<Region> selectionsList;


    public Label labelFileNameId, labelAmountOfColors, sizeWarning, filePath;

    public Slider sliderPosterizationScale;
    public TextField textFieldWidth, textFieldHeight;

    public PixelatedImageView photoView;
    public SelectBinding vboxSelectBinding;

    public JFXCheckBox checkBoxMarkBinding, checkBoxInvert, checkBoxFloaters;
    public JFXTextField textFieldXFloaters, textFieldYFloaters;
    public JFXButton toggleEditButton, togglePickerButton;
    public Label bindingsPath;

    /* User Interface Data */
    int imageWidth, imageHeight, posterizeScale;
    private String filename;
    public BufferedImage image;
    public FilteredImage filteredImage;
    public Stage stage;

    private UserInterfaceData userInterfaceData;
    private boolean isCustomFile;
    /* Handlers hier nodig?*/

    private EditPhotoEventHandlers editPhotoEventHandlers;
    BindingChangedEventHandler bindingChangedEventHandler;
    ChangeListener<Integer> markedColorChangeListener;
    ChangeListener<Boolean> showMarkingChangeListener;


    /* SELECTION */

    boolean editing = false;
    boolean picking = false;

    WritableImage writablePhotoview;

    private double XScroll;
    private double YScroll;

    private boolean onLoad;

    public JFXSpinner loadingSpinner;
    public Label threadCompleteCount;

    public double getXScroll() {
        return XScroll;
    }

    public double getYScroll() {
        return YScroll;
    }

    public void initialize(String path) throws IOException {
        this.onLoad = true;
        this.posterizeScale = 10;
        LoadFilteredImageController loader;

        if (FilenameUtils.getExtension(path).toLowerCase().equals("json")) {
            isCustomFile = true;
            loader = new LoadFilteredImageController(new File(path), this);
            this.image = loader.getFilteredImage().getOriginalImage();
            System.out.println("IF " + ((BindingFilter) loader.getFilteredImage().getFilters().findRGBFilter(BindingFilter.class)).getRegions());
        } else {
            isCustomFile = false;
            this.image = ImageIO.read(new File(path));
            loader = new LoadFilteredImageController(this.image, this.posterizeScale, false, Integer.parseInt(textFieldXFloaters.getText()), Integer.parseInt(textFieldYFloaters.getText()), this);
        }

        this.filteredImage = loader.getFilteredImage();

        //Forcing again....
        //((BindingFilter)this.filteredImage.getFilters().findRGBFilter(BindingFilter.class)).setRegions(lfic.getRegions());

        // UI
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();

        this.threadCompleteCount.setText(String.format("%s/%s", filteredImage.getThreadCount(), filteredImage.getThreadCount()));

        this.filename = path.substring(path.lastIndexOf("/") + 1);
        this.selectionsList.setCellFactory(param -> new SelectionListCell<>(filteredImage, this));
        this.selectionsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            BindingFilter bf = (BindingFilter) this.filteredImage.getFilters().findRGBFilter(BindingFilter.class);

            bf.getRegions().forEach(region -> region.setMarked(false));

            newValue.setMarked(true);

            filteredImage.redraw();
        });

        // Global
        this.stage = (Stage) anchorPaneWindow.getScene().getWindow();
        this.stage.setOnCloseRequest(event -> {
            try {
                SaveWarningResult saveWarningResult = openSaveWarningWindow(FilterDescription.JSON, true, false);

                if (saveWarningResult == SaveWarningResult.CANCEL) event.consume();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.editPhotoEventHandlers = new EditPhotoEventHandlers(this);
        this.editPhotoEventHandlers.initializeListeners();

        initializePhotoScale();
        if (isCustomFile) loader.loadDataInUserInterface();
        updateUserInterfaceText();
        updateBindingSelection();

        paneDefault.setExpanded(true);

        updateBindingsPath();
    }

    public void setScrollPane() {
        imageScrollPane.layout();
        anchorPaneWindow.layout();

        imageScrollPane.setVvalue(getYScroll());
        imageScrollPane.setHvalue(getXScroll());
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
        labelAmountOfColors.setText("Aantal tinten: " + ((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class)).getBindingsMap().keySet().size());
    }

    private void updateBindingSelection() {
        BindingFilter bf = ((BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class));
        this.vboxSelectBinding.setBindingsMap(bf.getBindingsMap(), bf);
    }

    private void redrawPhotoView() {
        writablePhotoview = SwingFXUtils.toFXImage(filteredImage.getModifiedImage(), null);
        photoView.setImage(writablePhotoview);

        if (onLoad) {
            setScrollPane();
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

    public void openBindingColorSelector(ActionEvent actionEvent) throws IOException {
        CreateWindow newWindow = new CreateWindow("Link kleuren met bindingen", 800.0, 600.0, "components/ColorBindingLinker.fxml", false, false);
        ((ColorBindingLinker) newWindow.getController()).initialize(this.filteredImage);
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
    }

    /* INTERFACE
     * ThreadListener
     */

    @Override
    public void OnRedrawBegin() {
        loadingSpinner.setVisible(true);
        this.threadCompleteCount.setVisible(true);
        this.threadCompleteCount.setText("0/0");
        vboxSelectBinding
                .getComboBoxLevels()
                .getSelectionModel()
                .selectedItemProperty()
                .removeListener(this.markedColorChangeListener);
        vboxSelectBinding.removeEventHandler(BindingChanged.BINDING_CHANGED, this.bindingChangedEventHandler);
    }

    @Override
    public void onThreadComplete() {
        Platform.runLater(() -> {
            this.threadCompleteCount.setText(String.format("%s/%s", filteredImage.getThreadsDone(), filteredImage.getThreadCount()));
        });
        //You could add a waiting symbol here
        //fuckingWerk();
        //redrawPhotoView(); //Optional, but shows the thread's progress in realtime
    }

    @Override
    public void onRedrawComplete() {
        Platform.runLater(
                () -> {
                    loadingSpinner.setVisible(false);
                    this.threadCompleteCount.setVisible(false);
                    updateBindingSelection();

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

        BindingFilter bf = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);

        if (bf.getRegions().contains(region)) { //We're editing rather than adding
            ((ChangeSelectionBinding) newWindow.getController()).initialize(this.filteredImage, region, true);
        } else {
            ((ChangeSelectionBinding) newWindow.getController()).initialize(this.filteredImage, region, false);
        }

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

    public void makeNewFile(ActionEvent actionEvent) throws IOException {
        SaveWarningResult saveWarningResult = openSaveWarningWindow(FilterDescription.JSON, true, false);

        if (saveWarningResult != SaveWarningResult.CANCEL) {
            Platform.runLater(() -> {
                try {
                    CreateFilePicker fp = new CreateFilePicker(ImageProperties.loadTitle, this.stage, ImageProperties.filterDescription, ImageProperties.filterExtensions, null);
                    File file = fp.getFile();

                    if (file != null) {
                        CreateWindow newWindow = new CreateWindow("Verilin | PhotoWeave", 800.0, 600.0, new FXMLLoader(getClass().getClassLoader().getResource("view/EditPhoto.fxml")), true, true);
                        newWindow.showWindow();
                        ((EditPhoto) newWindow.getController()).initialize(file.getAbsolutePath());
                    }

                    this.stage.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    public void openFile(ActionEvent actionEvent) throws IOException {
        SaveWarningResult saveWarningResult = openSaveWarningWindow(FilterDescription.JSON, true, false);

        if (saveWarningResult != SaveWarningResult.CANCEL) {
            Platform.runLater(() -> {
                CreateFilePicker fp = new CreateFilePicker(JsonProperties.loadTitle, this.stage, JsonProperties.filterDescription, JsonProperties.filterExtensions, null);
                File file = fp.getFile();

                if (file != null) {
                    CreateWindow newWindow = null;

                    try {
                        newWindow = new CreateWindow("Verilin | PhotoWeave", 800.0, 600.0, "view/EditPhoto.fxml", false, true);
                        newWindow.showWindow();
                        ((EditPhoto) newWindow.getController()).initialize(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.stage.close();
                }
            });
        }
    }

    public void saveFile(ActionEvent actionEvent) {
        openSaveWindow(FilterDescription.JSON);
    }

    public void exportImage(ActionEvent actionEvent) {
        openSaveWindow(FilterDescription.BITMAP);
    }

    public void closeWindow(ActionEvent actionEvent) throws IOException {
        openSaveWarningWindow(FilterDescription.JSON, true, false);
    }

    private void openSaveWindow(FilterDescription filterDescription) {
        System.out.println("SAVE FILE");
        CreateFilePicker fp;
        if (filterDescription == filterDescription.BITMAP) {
            fp = new CreateFilePicker(BitmapProperties.title, this.stage, BitmapProperties.filterDescription, BitmapProperties.filterExtensions, null);
        } else if (filterDescription == filterDescription.JSON) {
            System.out.println("JSON FILE");
            fp = new CreateFilePicker(JsonProperties.saveTitle, this.stage, JsonProperties.filterDescription, JsonProperties.filterExtensions, null);
        } else {
            fp = new CreateFilePicker(AllFilesProperties.saveTitle, this.stage, AllFilesProperties.filterDescription, AllFilesProperties.filterExtensions, null);
        }

        File file = fp.saveFile();

        if (file != null) {
            try {
                if (filterDescription == filterDescription.BITMAP) {
                    ImageIO.write(ImageUtil.convertImageToByteBinary(filteredImage.getModifiedImage()), "bmp", file);
                } else if (filterDescription == filterDescription.JSON) {
                    try {
                        collectUserInterfaceData();
                        System.out.println(file.getAbsolutePath());
                        new SaveFilteredImageController(this.filteredImage, this.userInterfaceData).save(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            } catch (IOException ex) {
            }
        }
    }

    private void openStartWindow() throws IOException {
        Platform.runLater(() -> {
            CreateWindow newWindow = null;
            try {
                newWindow = new CreateWindow("Verilin | PhotoWeave", 0, 0, "view/OpenPhoto.fxml", false, true);
                ((OpenPhoto) newWindow.getController()).initialize();
                newWindow.showWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private SaveWarningResult openSaveWarningWindow(FilterDescription filterDescription, boolean closeWindow, boolean newWindow) throws IOException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Waarschuwing!");
        alert.setHeaderText("U zal de huidige pagina verlaten. Wijzigingen aan uw bestand kunnen mogelijks verloren raken.\nWilt u uw bestand eerst opslaan?");
        alert.setContentText("Druk op opslaan om uw bestand op te slaan of negeren om uw wijzigingen te annuleren.\nDruk annuleren om verder te gaan met uw huidige bestand.");

        ButtonType buttonSave = new ButtonType("Opslaan");
        ButtonType buttonIgnore = new ButtonType("Negeren");
        ButtonType buttonCancel = new ButtonType("Annuleer", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonSave, buttonIgnore, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();

        SaveWarningResult saveWarningResult = SaveWarningResult.SAVE;

        if (result.get() == buttonSave) {
            openSaveWindow(filterDescription);
            whatWithWindow(closeWindow, newWindow);
            saveWarningResult = SaveWarningResult.SAVE;
        } else if (result.get() == buttonIgnore) {
            whatWithWindow(closeWindow, newWindow);
            saveWarningResult = SaveWarningResult.IGNORE;
        } else if (result.get() == buttonCancel) {
            alert.close();
            saveWarningResult = SaveWarningResult.CANCEL;
        }

        return saveWarningResult;
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

    public void removeSelections(ActionEvent actionEvent) {
        BindingFilter bindingFilter = (BindingFilter) filteredImage.getFilters().findRGBFilter(BindingFilter.class);

        for (Region region : bindingFilter.getRegions()) {
            region.setMarked(false);
        }

        filteredImage.redraw();
        selectionsList.getSelectionModel().clearSelection();
    }

    public void changeBindingsPath(ActionEvent actionEvent) {
        File newPath = new CreateDirectoryPicker("Kies map naar bindings").show();

        if (newPath != null) {
            ConfigUtil.setBindingsPath(newPath.getAbsolutePath());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Hint!");
        alert.setContentText("Het is aangeraden dat je je werk opslaat en PhotoWeave herstart om de nieuwe bindingenmap toe te passen. De gekozen bindingen vooraleer de map werd aangepast, blijven behouden.");
        alert.show();

        updateBindingsPath();
    }

    private enum SaveWarningResult {
        SAVE,
        IGNORE,
        CANCEL
    }

    private void updateBindingsPath() {
        bindingsPath.setText(ConfigUtil.getBindingsPath());
    }
}