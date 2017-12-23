package be.howest.photoweave.model.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class CreateFilePicker {
    private String title;
    private String startFolder;
    private String filterDescription;
    private List<String> filterExtensions;
    private FileChooser fileChooser;

    private Stage stage;

    public CreateFilePicker(String title, Stage stage, String filterDescription, List<String> filterExtensions) {
        this.title = title;
        this.startFolder = "user.home";
        this.stage = stage;
        this.filterDescription = filterDescription;
        this.filterExtensions = filterExtensions;

        createWindow();
    }

    private void createWindow() {
        fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(filterDescription, filterExtensions));
        fileChooser.setInitialDirectory(new File(System.getProperty(startFolder)));
    }

    public File getFile() {
        return fileChooser.showOpenDialog(stage);
    }

    public File saveFile() {
        return fileChooser.showSaveDialog(stage);
    }
}
