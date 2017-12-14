package be.howest.photoweave.controllers;

import be.howest.photoweave.model.imaging.FilteredImage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.event.ChangeListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Objects;

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
    public JFXButton buttonConfirm;

    private NumberFormat integerFormat;
    private NumberFormat decimalFormat;

    private FilteredImage filteredImage;

    private int weefBreedteGeheel;
    private int weefHoogteGeheel;

    public void initialize(FilteredImage filteredImage) {
        this.filteredImage = filteredImage;

        integerFormat = DecimalFormat.getInstance();
        integerFormat.setParseIntegerOnly(true);

        decimalFormat = new DecimalFormat( "#.0" );

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
        //txtBreedteCm.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));
        //txtHoogteCm.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));
        txtInslagenCm.setTextFormatter(new TextFormatter<>(this::integerTextFormatter));
        //txtWeefHoogte.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));
        //txtWeefverhouding.setTextFormatter(new TextFormatter<>(this::decimalTextFormatter));

        txtMaxBreedte.textProperty().addListener(this::recalculate);
        txtWeefBreedte.textProperty().addListener(this::recalculate);
        txtDradenCm.textProperty().addListener(this::recalculate);
        txtInslagenCm.textProperty().addListener(this::recalculate);

        buttonConfirm.setDisable(true);
    }

    private void recalculate(Observable observable, String oldValue, String newValue) {
        //Only calculate if all values are set
        if (txtMaxBreedte.textProperty().get().equals("")
                ||  txtWeefBreedte.textProperty().get().equals("")
                ||  txtDradenCm.textProperty().get().equals("")
                ||  txtInslagenCm.textProperty().get().equals(""))
            return;

        int maxBreedte = 0;
        double dradenCm = 0;
        double inslagenCm = 0;

        try {
            maxBreedte = integerFormat.parse(txtMaxBreedte.textProperty().get()).intValue();
            weefBreedteGeheel = integerFormat.parse(txtWeefBreedte.textProperty().get()).intValue();
            dradenCm = decimalFormat.parse(txtDradenCm.textProperty().get()).doubleValue();
            inslagenCm = decimalFormat.parse(txtInslagenCm.textProperty().get()).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        double verhouding  =
                ((double) filteredImage.getOriginalImage().getWidth()
                / (double) filteredImage.getOriginalImage().getHeight());
        double breedteCm = weefBreedteGeheel / dradenCm;
        double hoogteCm = breedteCm / verhouding;
        double weefHoogte = hoogteCm * inslagenCm;
        weefHoogteGeheel = (int) Math.round(weefHoogte);

        double weefVerhouding = weefBreedteGeheel / weefHoogte;

        txtBreedteCm.setText(String.format("%.1f", breedteCm));
        txtHoogteCm.setText(String.format("%.1f", hoogteCm));
        txtWeefHoogte.setText(String.format("%.1f", weefHoogte));
        txtWeefverhouding.setText(String.format("%.2f", weefVerhouding));
        lblNieuweGrootte.setText(String.format("%spx x %spx", weefBreedteGeheel, weefHoogteGeheel));

        buttonConfirm.setDisable(false);
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
        return textFormatter(integerFormat, c);
    }


    private TextFormatter.Change decimalTextFormatter(TextFormatter.Change c) {
        return textFormatter(decimalFormat, c);
    }

    public void confirmChanges(ActionEvent actionEvent) {
        Stage stage = (Stage) buttonConfirm.getScene().getWindow();

        filteredImage.resize(weefBreedteGeheel, weefHoogteGeheel);

        stage.close();
    }

    public void cancelChanges(ActionEvent actionEvent) {
        Stage stage = (Stage) buttonConfirm.getScene().getWindow();

        stage.close();
    }
}
