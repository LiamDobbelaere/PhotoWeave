package be.howest.photoweave.components.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Created by tomdo on 7/11/2017.
 */
public class BindingChanged extends Event {
    public static final EventType<BindingChanged> BINDING_CHANGED = new EventType<>(ANY);

    public BindingChanged() {
        super(BINDING_CHANGED);
    }

    public void invokeHandler(BindingChangedEventHandler handler) {
        handler.onBindingChanged();
    }
}