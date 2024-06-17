package dev.lugami.practice.utils.event;

import org.bukkit.event.Cancellable;

public class CancellableEvent extends BaseEvent implements Cancellable {

    private boolean cancelled;

    public CancellableEvent() {
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
