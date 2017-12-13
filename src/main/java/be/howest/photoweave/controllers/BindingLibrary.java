package be.howest.photoweave.controllers;


import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingFactory;
import be.howest.photoweave.model.util.ImageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BindingLibrary {
    public Accordion acco;

    private int BINDING_SIZE = 60;
    private int X_TILES = 5;

    private BindingRepresentation[][] grid;

    private ObservableList<Binding> bindings = FXCollections.observableArrayList();

    private Binding SELECTED_BINDING = null;

    public void initialize(){
        this.bindings.addAll(new BindingFactory().getOptimizedBindings());

        AnchorPane newPanelContent = new AnchorPane();
        newPanelContent.getChildren().add(new Label(""));
        TitledPane defaultBindings = new TitledPane("Default Bindings",newPanelContent);
        acco.getPanes().add(defaultBindings);

        grid = new BindingRepresentation[X_TILES][(this.bindings.size()/X_TILES)];

        int x = 0;
        int y = 0;
        for (int i = 0; i < this.bindings.size(); i++) {
            System.out.println(x);
            grid[x][y] = new BindingRepresentation(x, y, false, true, this.bindings.get(i));
            newPanelContent.getChildren().add(grid[x][y]);

            if (x < 4) x++;
            else {x = 0; y++;}
        }

    }

    public void applyBinding(MouseEvent mouseEvent) {
        System.out.println(SELECTED_BINDING);
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

            Label label2 = new Label("TEKSTNAAM.JPG");
            Tooltip tooltip = new Tooltip("TEKSTNAAM.JPG");
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
