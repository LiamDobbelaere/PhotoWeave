package be.howest.photoweave.model;

import be.howest.photoweave.model.imaging.ThreadEventListener;

public interface ParametersInterface extends ThreadEventListener {
    void setInvert(boolean bool);
}
