package dev.lugami.practice.utils;

import com.mysql.jdbc.TimeUtil;
import lombok.Data;

@Data
public class Cooldown {

    private final long start = System.currentTimeMillis();
    private final long expire;
    private boolean notified;

    /**
     * Creates a new cooldown with the specified duration.
     *
     * @param duration The duration of the cooldown in milliseconds.
     */
    public Cooldown(long duration) {
        this.expire = this.start + duration;

        if (duration == 0) {
            this.notified = true;
        }
    }

    /**
     * Gets the time passed since the cooldown started.
     *
     * @return The time passed in milliseconds.
     */
    public long getPassed() {
        return System.currentTimeMillis() - this.start;
    }

    /**
     * Gets the remaining time for the cooldown.
     *
     * @return The remaining time in milliseconds.
     */
    public long getRemaining() {
        return this.expire - System.currentTimeMillis();
    }

    /**
     * Checks if the cooldown has expired.
     *
     * @return true if the cooldown has expired, false otherwise.
     */
    public boolean hasExpired() {
        return System.currentTimeMillis() >= this.expire;
    }

    /**
     * Gets a human-readable representation of the remaining cooldown time.
     *
     * @return The time left in a human-readable format.
     */
    public String getTimeLeft() {
        if (this.getRemaining() >= 60_000) {
            return TimeUtils.formatTime(this.getRemaining());
        } else {
            return TimeUtils.millisToTime(this.getRemaining());
        }
    }
}