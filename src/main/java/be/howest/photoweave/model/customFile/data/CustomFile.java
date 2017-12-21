package be.howest.photoweave.model.customFile.data;


public class CustomFile {
    private ImageData image;
    private Mutation mutation;
    private UserInterfaceData userInterface;

    public CustomFile(ImageData image, Mutation mutation, UserInterfaceData userInterface) {
        this.image = image;
        this.mutation = mutation;
        this.userInterface = userInterface;
    }
}