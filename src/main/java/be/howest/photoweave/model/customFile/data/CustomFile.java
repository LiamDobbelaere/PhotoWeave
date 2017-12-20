package be.howest.photoweave.model.customFile.data;


public class CustomFile {
    private ImageData image;
    private Mutation mutation;
    private UserInterface userInterface;

    public CustomFile(ImageData image, Mutation mutation, UserInterface userInterface) {
        this.image = image;
        this.mutation = mutation;
        this.userInterface = userInterface;
    }
}