package be.howest.photoweave.controllers;


import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.util.ImageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BindingLibrary {
    public Accordion acco;
    public TextField txtSearchBinding;

    private int BINDING_SIZE = 60;
    private int X_TILES = 5;

    private BindingRepresentation[][] grid;

    private ObservableList<Binding> bindings = FXCollections.observableArrayList();

    private Binding SELECTED_BINDING = null;

    public void initialize(){
        HashMap<String, List<Binding>> allBindings = new BindingFactory().getAllBindings();
        this.bindings.addAll(new BindingFactory().getOptimizedBindings());
        allBindings.forEach((key, binding) -> {
            System.out.println("AllB " + key + " " + binding.size());
            AnchorPane newPanelContent = new AnchorPane();
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.getStyleClass().setAll("scrollpane","edge-to-edge");
            newPanelContent.getChildren().add(new Label(""));
            scrollPane.setContent(new Group(newPanelContent));
            TitledPane defaultBindings = new TitledPane(key,scrollPane);
            acco.getPanes().add(defaultBindings);

            //grid = new BindingRepresentation[X_TILES][(this.bindings.size()/X_TILES)];
            grid = new BindingRepresentation[100][100];

            int x = 0;
            int y = 0;
            for (int i = 0; i < binding.size(); i++) {
                System.out.println(y);
                grid[x][y] = new BindingRepresentation(x, y, false, true, binding.get(i));
                newPanelContent.getChildren().add(grid[x][y]);
                if (x < 4) x++;
                else {x = 0; y++;}
            }
        });

/*
        AnchorPane newPanelContent = new AnchorPane();
        ScrollPane scrollPane = new ScrollPane();
        newPanelContent.getChildren().add(new Label(""));
        scrollPane.setContent(new Group(newPanelContent));
        TitledPane defaultBindings = new TitledPane("Default Bindings",scrollPane);
        acco.getPanes().add(defaultBindings);

        //grid = new BindingRepresentation[X_TILES][(this.bindings.size()/X_TILES)];
        grid = new BindingRepresentation[100][100];

        int x = 0;
        int y = 0;
        for (int i = 0; i < this.bindings.size(); i++) {
            System.out.println(y);
            grid[x][y] = new BindingRepresentation(x, y, false, true, this.bindings.get(i));
            newPanelContent.getChildren().add(grid[x][y]);
            if (x < 4) x++;
            else {x = 0; y++;}
        }
*/

    }

    public void applyBinding(MouseEvent mouseEvent) {
        System.out.println(SELECTED_BINDING);
    }

    public void searchBinding(KeyEvent keyEvent) {
        acco.getPanes().clear();
        if (Objects.equals(txtSearchBinding.textProperty().getValue().replaceAll("\\s", ""), "")){
            System.out.println("INIT");
            initialize();
        } else {
            AnchorPane newPanelContent = new AnchorPane();
            ScrollPane scrollPane = new ScrollPane();
            newPanelContent.getChildren().add(new Label(""));
            scrollPane.setContent(new Group(newPanelContent));
            TitledPane defaultBindings = new TitledPane("GEVONDEN BINDINGS",scrollPane);
            defaultBindings.setAnimated(false);
            acco.setExpandedPane(defaultBindings);
            acco.getPanes().add(defaultBindings);

            //grid = new BindingRepresentation[X_TILES][(this.bindings.size()/X_TILES)];
            grid = new BindingRepresentation[100][100];

            int x = 0;
            int y = 0;

            List<Binding> filtered = new ArrayList<>();
            this.bindings.forEach(binding -> {
                if (binding.getName().contains(txtSearchBinding.textProperty().getValue())){
                    filtered.add(binding);
                }
            });

            for (int i = 0; i < filtered.size(); i++) {
                System.out.println(y);
                grid[x][y] = new BindingRepresentation(x, y, false, true, filtered.get(i));
                newPanelContent.getChildren().add(grid[x][y]);
                if (x < 4) x++;
                else {x = 0; y++;}
            }

        }
        System.out.println(txtSearchBinding.textProperty().getValue());
    }

    private class BindingRepresentation extends VBox {
        private int x, y;
        private boolean isFilled;
        private Binding binding;

        BindingRepresentation(int x, int y, boolean isFilled, boolean hasStroke, Binding binding) {
            this.x = x;
            this.y = y;
            this.isFilled = isFilled;
            this.binding = binding;

            Label label1 = new Label();
            label1.relocate(this.x * BINDING_SIZE, this.y * BINDING_SIZE);
            label1.setGraphic(new ImageView(ImageUtil.resample(SwingFXUtils.toFXImage(this.binding.getBindingImage(),null),4)));

            Label label2 = new Label(binding.getName());
            Tooltip tooltip = new Tooltip(binding.getName());
            Tooltip.install(this, tooltip);

            getChildren().addAll(label1,label2);

            this.setTranslateX(this.x * (BINDING_SIZE * 2));
            this.setTranslateY(this.y * (BINDING_SIZE * 2));
            setOnMouseClicked(e -> fill());
        }

        void fill() {
            for (BindingRepresentation[] bindingRepresentations : grid) {
                for (BindingRepresentation b : bindingRepresentations){
                b.setBorder(new Border(new BorderStroke(Color.GREY,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            }
            }
            this.isFilled = !this.isFilled;
            this.setBorder(new Border(new BorderStroke(Color.GREEN,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            SELECTED_BINDING = this.binding;

        }
    }


}
