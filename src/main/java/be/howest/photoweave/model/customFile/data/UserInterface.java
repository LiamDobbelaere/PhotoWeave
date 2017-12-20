package be.howest.photoweave.model.customFile.data;

public class UserInterface {
    private ImageViewParams imageview;
    private BindingParams binding;

    private UserInterface(ImageViewParams imageview, BindingParams binding) {
        this.imageview = imageview;
        this.binding = binding;
    }
}
