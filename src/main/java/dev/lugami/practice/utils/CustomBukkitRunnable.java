package dev.lugami.practice.utils;

import dev.lugami.practice.Budget;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class CustomBukkitRunnable extends BukkitRunnable {

    @Override
    public abstract void run();

    public enum Mode {
        NOW,
        LATER,
        TIMER
    }

    public enum Type {
        ASYNC,
        SYNC
    }

    private final Mode mode;
    private final Type type;

    private long later;
    private long period;
    private long delay;

    public CustomBukkitRunnable(Mode mode, Type type) {
        if (mode != Mode.NOW) {
            throw new IllegalArgumentException("task mode must be NOW, since no ticks were set up.");
        }
        this.mode = mode;
        this.type = type;
    }

    public CustomBukkitRunnable(Mode mode, Type type, long later) {
        if (mode != Mode.LATER) {
            throw new IllegalArgumentException("task mode must be LATER, since a later ticking was set up.");
        }
        this.mode = mode;
        this.type = type;
        this.later = later;
    }

    public CustomBukkitRunnable(Mode mode, Type type, long period, long delay) {
        if (mode != Mode.TIMER) {
            throw new IllegalArgumentException("task mode must be TIMER, since a 2 ticking timers were set up.");
        }
        this.mode = mode;
        this.type = type;
        this.period = period;
        this.delay = delay;
    }

    public void execute() {
        try {
            if (type == Type.SYNC) {
                switch (mode) {
                    case LATER:
                        TaskUtil.runTaskLater(this, later);
                        break;
                    case TIMER:
                        TaskUtil.runTaskTimer(this, delay, period);
                        break;
                    case NOW:
                        TaskUtil.runTask(this);
                        break;
                }
                Budget.getInstance().getLogger().info("Started a task on " + this.getClass().getSimpleName());
            } else if (type == Type.ASYNC) {
                switch (mode) {
                    case LATER:
                        TaskUtil.runTaskLaterAsynchronously(this, later);
                        break;
                    case TIMER:
                        TaskUtil.runTaskTimerAsynchronously(this, delay, period);
                        break;
                    case NOW:
                        TaskUtil.runTaskAsynchronously(this);
                        break;
                }
                Budget.getInstance().getLogger().info("Started a async task on " + this.getClass().getSimpleName());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
