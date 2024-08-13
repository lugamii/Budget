package dev.lugami.practice.profile.misc;

import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.SoundAPI;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class DiscMetadata {

    private final Profile profile;
    private SoundAPI.Disc disc;
    private boolean finished = true;
    private int secondsPassed = 0;
    private int songSeconds;
    private BukkitTask task;
    private BukkitTask delayTask;

    public DiscMetadata(Profile profile, SoundAPI.Disc disc) {
        this.profile = profile;
        this.disc = disc;
        start(true);
    }

    public void start(boolean skipDelay) {
        if (this.profile != null && this.profile.getProfileOptions().isToggled(Settings.LOBBY_MUSIC)) {
            // Cancel any existing tasks
            if (task != null) task.cancel();
            if (delayTask != null) delayTask.cancel();

            // Start the playback task
            this.task = TaskUtil.runTaskTimerAsynchronously(this::handleDiscPlayback, 0, 20); // Run every second

            // Play the disc immediately if specified
            play(skipDelay);
        }
    }

    private void handleDiscPlayback() {
        if (disc != null) {
            this.secondsPassed++;
            if (this.secondsPassed > this.disc.getLengthInSeconds()) {
                // Song has finished
                this.secondsPassed = 0;
                this.disc = null;
                this.finished = true;
                // Schedule the next song to play after a delay
                scheduleNextSong();
            } else {
                this.finished = false;
            }
        } else if (this.disc == null && this.finished) {
            // No disc is playing and previous disc has finished
            play(false); // Play a new song after the delay
        }
    }

    private void scheduleNextSong() {
        // Schedule the next song only if the current disc has finished
        if (this.finished && this.secondsPassed > this.songSeconds) {
            if (this.delayTask != null) this.delayTask.cancel();
            this.delayTask = TaskUtil.runTaskLater(() -> {
                play(false); // Play a new song after 5 seconds
            }, 20 * 5L); // 5-second delay
        }
    }

    public void play(boolean skipDelay) {
        // Cancel any scheduled delay
        if (this.delayTask != null) this.delayTask.cancel();

        // Stop the previous song if it’s playing
        if (this.task != null) this.task.cancel();

        // Start a new song
        if (disc == null) this.disc = SoundAPI.Disc.getRandom();
        this.songSeconds = this.disc.getLengthInSeconds();
        SoundAPI.playSong(this.profile.getPlayer(), this.disc);

        // Reset playback state
        this.secondsPassed = 0;
        this.finished = false;

        // Restart the playback task
        this.task = TaskUtil.runTaskTimerAsynchronously(this::handleDiscPlayback, 0, 20); // Run every second

        // Schedule the next song if not skipping delay
        if (!skipDelay) {
            scheduleNextSong();
        }
    }

    public void stop() {
        for (int i = 0; i < 5; i++) SoundAPI.stopSong(this.profile.getPlayer());
        if (this.task != null) this.task.cancel();
        if (this.delayTask != null) this.delayTask.cancel();
        this.disc = null;
        this.secondsPassed = 0;
        this.songSeconds = 0;
        this.finished = true;
    }
}
