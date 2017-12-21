package be.howest.photoweave.model.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateFilePicker {
    private String title;
    private String startFolder;
    private String filterDescription;
    private List<String> filterExtentions;
    private FileChooser fileChooser;

    private Stage stage;

    public CreateFilePicker(String title, String startFolder, Stage stage, String filterDescription, List<String> filterExtentions) {
        this.title = title;
        this.startFolder = startFolder;
        this.stage = stage;
        this.filterDescription = filterDescription;
        this.filterExtentions = filterExtentions;

        createWindow();
    }

    private void createWindow() {
        fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(filterDescription, filterExtentions));
        fileChooser.setInitialDirectory(new File(System.getProperty(startFolder)));
    }

    public File getFile() {
        return fileChooser.showOpenDialog(stage);
    }

    public File saveFile() {
        return fileChooser.showSaveDialog(stage);
    }
}
