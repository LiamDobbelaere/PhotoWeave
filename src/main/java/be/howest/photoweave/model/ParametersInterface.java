package be.howest.photoweave.model;

import be.howest.photoweave.model.imaging.ThreadEventListener;

public interface ParametersInterface extends ThreadEventListener {
    void setUIComponentInverted(boolean bool);

    void setUIComponentMarked(boolean asBoolean);

    void setUICompentenPosterize(int asInt);

    void setUIComponentHeight(double asDouble);

    void setUIComponentWidth(double asDouble);

    void setUIComponentXScroll(double asDouble);

    void setUIComponentYScroll(double asDouble);
}
