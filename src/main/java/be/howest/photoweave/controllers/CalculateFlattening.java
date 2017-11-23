package be.howest.photoweave.controllers;

import be.howest.photoweave.model.imaging.FilteredImage;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;

import javax.swing.event.ChangeListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by tomdo on 22/11/2017.
 */
public class CalculateFlattening {
    public Label lblOudeGrootte;
    public JFXTextField txtMaxBreedte;
    public JFXTextField txtWeefBreedte;
    public JFXTextField txtDradenCm;
    public JFXTextField txtBreedteCm;
    public JFXTextField txtHoogteCm;
    public JFXTextField txtInslagenCm;
    public JFXTextField txtWeefHoogte;
    public JFXTextField txtWeefverhouding;
    public Label lblNieuweGrootte;

    public void initialize(FilteredImage filteredImage) {
        lblOudeGrootte.setText(String.format("%spx x %spx", filteredImage.getOriginalImage().getWidth(), filteredImage.getOriginalImage().getHeight()));
        txtMaxBreedte.setText("");
        txtWeefBreedte.setText("");
        txtDradenCm.setText("");
        txtBreedteCm.setText("");
        txtHoogteCm.setText("");
        txtInslagenCm.setText("");
        txtWeefHoogte.setText("");
        txtWeefverhouding.setText("");
        lblNieuweGrootte.setText("");

        txtMaxBreedte.setTextFormatter(new TextFormatter<>(this::integerTextFormatter));
        txtWeefBreedte.setTextFormatter(new TextFormatter<>(this::integerTextFormatter));
        txtDradenCm.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));
        txtBreedteCm.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));
        txtHoogteCm.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));
        txtInslagenCm.setTextFormatter(new TextFormatter<>(this::integerTextFormatter));
        txtWeefHoogte.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));
        txtWeefverhouding.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));

        txtMaxBreedte.textProperty().addListener(this::recalculate);
        txtWeefBreedte.textProperty().addListener(this::recalculate);
        txtDradenCm.textProperty().addListener(this::recalculate);
        txtInslagenCm.textProperty().addListener(this::recalculate);
    }

    private ChangeListener<> recalculate(Observable observable, String oldValue, String newValue) {
        
    }

    private TextFormatter.Change textFormatter(NumberFormat format, TextFormatter.Change c) {
        if ( c.getControlNewText().isEmpty() )
        {
            return c;
        }

        ParsePosition parsePosition = new ParsePosition( 0 );
        Object object = format.parse( c.getControlNewText(), parsePosition );

        if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
        {
            return null;
        }
        else
        {
            return c;
        }
    }

    private TextFormatter.Change integerTextFormatter(TextFormatter.Change c) {
        NumberFormat decimalFormat = DecimalFormat.getInstance();
        decimalFormat.setParseIntegerOnly(true);

        return textFormatter(decimalFormat, c);
    }


    private TextFormatter.Change decimalTextFormatter(TextFormatter.Change c) {
        return textFormatter(new DecimalFormat( "#.0" ), c);
    }
}
