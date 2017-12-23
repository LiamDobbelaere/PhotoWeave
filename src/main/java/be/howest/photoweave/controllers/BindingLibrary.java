package be.howest.photoweave.controllers;


import be.howest.photoweave.components.BindingMaker;
import be.howest.photoweave.model.util.CreateWindow;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.util.ImageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BindingLibrary {
    public Accordion accordion;
    public TextField txtSearchBinding;
    public Label lblSelectedBinding;
    public ImageView imgSelectedBinding;
    public Button btnApply;
    public ScrollPane scrollPane;

    //Pass on to the parent controller
    public Binding PASSED_BINDING;
    public boolean applyBinding;
    public AnchorPane anchorPaneWindow;
    private Stage stage;


    private ObservableList<Binding> tempBinding = FXCollections.observableArrayList();
    private BindingRepresentation PREVIOUS_BIND_REP = null;

    public void initialize(Binding passedBinding){
        scrollPane.setFitToWidth(true);

        this.applyBinding = false;
        this.PASSED_BINDING = passedBinding;

        this.imgSelectedBinding = new ImageView(ImageUtil.resample(SwingFXUtils.toFXImage(PASSED_BINDING.getBindingImage(),null),4));
        this.lblSelectedBinding.setText(passedBinding.getName());

        this.tempBinding.clear();
        this.tempBinding.addAll(BindingFactory.getInstance().getBindings());

        this.stage = (Stage) anchorPaneWindow.getScene().getWindow();

        HashMap<String, List<Binding>> allBindings = BindingFactory.getInstance().getAllBindings();
        allBindings.forEach(this::generateAccordion);
    }

    private void generateAccordion(String key, List<Binding> bindings) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().setAll("scrollpane","edge-to-edge");
        scrollPane.setMaxHeight(350);

        TitledPane paneLibrary = new TitledPane(key,scrollPane);
        paneLibrary.setAnimated(false);

        accordion.getPanes().add(paneLibrary);
        if (key.toLowerCase().equals("default") || key.toLowerCase().equals("gevonden bindings")) accordion.setExpandedPane(paneLibrary);

        scrollPane.setContent(generateBindingGrid(bindings));
    }

    private void generateSearchAccordion(){
        List<Binding> filtered = new ArrayList<>();
        this.tempBinding.forEach(binding -> {
            if (binding.getName().toLowerCase().contains(txtSearchBinding.textProperty().getValue().toLowerCase())){
                filtered.add(binding);
            }
        });

        HashMap<String, List<Binding>> searched = new HashMap<>();
        searched.put("GEVONDEN BINDINGS",filtered);
        searched.forEach(this::generateAccordion);
    }

    private Group generateBindingGrid(List<Binding> bindings){
        int x = 0;
        int y = 0;
        GridPane grid = new GridPane();
        for (int i = 0; i < bindings.size(); i++) {
            BindingRepresentation br = new BindingRepresentation(x, y, false, (i % 2 == 0), bindings.get(i));
            br.setOnMouseClicked(this::selectBindingRepresentation);
            grid.add(br,x,y);
            if (x < 4) x++;
            else {x = 0; y++;}
        }
        return new Group(grid);
    }


    private void selectBindingRepresentation(MouseEvent mouseEvent) {
        BindingRepresentation selectedBindRep = (BindingRepresentation)mouseEvent.getSource();
        selectedBindRep.setStyle("-fx-background-color: teal;");

        if (PREVIOUS_BIND_REP != null) PREVIOUS_BIND_REP.setStyle((PREVIOUS_BIND_REP.isOdd)?"-fx-background-color: #FFF;":"-fx-background-color: #CCC;");

        imgSelectedBinding = new ImageView(ImageUtil.resample(SwingFXUtils.toFXImage(selectedBindRep.getBinding().getBindingImage(),null),4));
        lblSelectedBinding.setText(selectedBindRep.getBinding().getName());

        PREVIOUS_BIND_REP = selectedBindRep;
        PASSED_BINDING = selectedBindRep.getBinding();
    }

    public void applyBinding(MouseEvent mouseEvent) {
        applyBinding = true;
        Stage stage = (Stage) btnApply.getScene().getWindow();
        stage.close();
    }

    public void searchBinding(KeyEvent keyEvent) {
        accordion.getPanes().clear();
        if (Objects.equals(txtSearchBinding.textProperty().getValue().replaceAll("\\s", ""), "")){
            initialize(PASSED_BINDING);
        } else {
            generateSearchAccordion();
        }
    }

    public void openBindingCreator(ActionEvent actionEvent) throws IOException {
        CreateWindow newWindow = new CreateWindow("PhotoWeave | Maak Binding", 800.0, 600.0, "components/BindingMaker.fxml", false, false);
        ((BindingMaker) newWindow.getController()).initialize();
        newWindow.focusWaitAndShowWindow(this.stage.getScene().getWindow(), Modality.APPLICATION_MODAL);
    }


    private class BindingRepresentation extends StackPane {
        private int x, y;
        private boolean isOdd;
        private boolean isFilled;
        private Binding binding;

        BindingRepresentation(int x, int y, boolean isFilled, boolean isOdd, Binding binding) {
            this.x = x;
            this.y = y;
            this.isOdd = isOdd;
            this.isFilled = isFilled;
            this.binding = binding;

            Label label1 = new Label(binding.getName());
            label1.relocate(this.x * 60, this.y * 60);

            ImageView iv = new ImageView(ImageUtil.resample(SwingFXUtils.toFXImage(this.binding.getBindingImage(),null),4));
            label1.setGraphic(iv);
            label1.setContentDisplay(ContentDisplay.TOP);

            Tooltip tooltip = new Tooltip(binding.getName());
            Tooltip.install(this, tooltip);

            getChildren().addAll(label1);

            this.setStyle((this.isOdd)?"-fx-background-color: #FFF;":"-fx-background-color: #CCC;");

            this.setMinWidth(114);
            this.setMinHeight(114);

            setOnMouseClicked(e -> fill());
        }

        private void fill() {
            this.isFilled = !this.isFilled;
            this.setStyle("-fx-background-color: teal;");
        }

        public Binding getBinding() {
            return binding;
        }
    }
}
