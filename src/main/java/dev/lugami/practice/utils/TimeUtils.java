package dev.lugami.practice.utils;

public class TimeUtils {

    /**
     * Converts milliseconds to a formatted time string (mm:ss).
     *
     * @param millis the time in milliseconds
     * @return formatted time string (mm:ss)
     */
    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Converts milliseconds to a human-readable time string.
     *
     * @param millis the time in milliseconds
     * @return human-readable time string
     */
    public static String millisToTime(long millis) {
        if (millis < 1000) {
            return millis + " millisecond" + (millis != 1 ? "s" : "");
        } else if (millis < 60 * 1000) {
            long seconds = millis / 1000;
            return seconds + " second" + (seconds != 1 ? "s" : "");
        } else if (millis < 60 * 60 * 1000) {
            long minutes = millis / (60 * 1000);
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            long hours = millis / (60 * 60 * 1000);
            return hours + " hour" + (hours != 1 ? "s" : "");
        }
    }

}
