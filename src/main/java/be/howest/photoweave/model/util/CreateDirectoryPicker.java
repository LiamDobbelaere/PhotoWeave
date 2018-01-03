package be.howest.photoweave.model.util;

import javafx.stage.DirectoryChooser;

import java.io.File;

public class CreateDirectoryPicker {
    private DirectoryChooser chooser;


    public CreateDirectoryPicker(String title) {
        chooser = new DirectoryChooser();
        chooser.setTitle(title);
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    public File show() {
        return chooser.showDialog(null);
    }
}
