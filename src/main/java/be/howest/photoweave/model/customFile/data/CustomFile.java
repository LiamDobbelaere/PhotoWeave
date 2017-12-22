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

    public ImageData getImage() {
        return image;
    }

    public Mutation getMutation() {
        return mutation;
    }

    public UserInterfaceData getUserInterface() {
        return userInterface;
    }


}