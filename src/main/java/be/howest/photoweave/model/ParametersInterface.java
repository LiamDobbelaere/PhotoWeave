package be.howest.photoweave.model;

import be.howest.photoweave.model.imaging.ThreadEventListener;

public interface ParametersInterface extends ThreadEventListener {
    void setUIComponentInverted(boolean bool);

    void setUIComponentMarked(boolean asBoolean);

    void setUICompentenPosterize(int asInt);

    void setUIComponentViewHeight(double asDouble);

    void setUIComponentViewWidth(double asDouble);

    void setUIComponentXScroll(double asDouble);

    void setUIComponentYScroll(double asDouble);

    void setUIComponentXFloater(int asInt);

    void setUIComponentYFloater(int asInt);

    void setUIComponentWidth(int width);

    void setUIComponentHeight(int height);
}
