package be.howest.photoweave.components.events;

import javafx.event.EventHandler;

/**
 * Created by tomdo on 7/11/2017.
 */

public abstract class BindingChangedEventHandler implements EventHandler<BindingChanged> {
    public abstract void onBindingChanged();

    @Override
    public void handle(BindingChanged event) {
        event.invokeHandler(this);
    }
}
