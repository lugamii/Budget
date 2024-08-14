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
            if (task != null) task.cancel();
            if (delayTask != null) delayTask.cancel();
            this.task = TaskUtil.runTaskTimerAsynchronously(this::handleDiscPlayback, 0, 20);
            play(skipDelay);
        }
    }

    private void handleDiscPlayback() {
        if (disc != null) {
            this.secondsPassed++;
            if (this.secondsPassed > this.disc.getLengthInSeconds()) {
                this.secondsPassed = 0;
                this.disc = null;
                this.finished = true;
                scheduleNextSong();
            } else {
                this.finished = false;
            }
        } else if (this.disc == null && this.finished) {
            play(false);
        }
    }

    private void scheduleNextSong() {
        // Schedule the next song only if the current disc has finished
        if (this.finished && this.secondsPassed > this.songSeconds) {
            if (this.delayTask != null) this.delayTask.cancel();
            this.delayTask = TaskUtil.runTaskLater(() -> {
                play(false);
            }, 20 * 5L);
        }
    }

    public void play(boolean skipDelay) {
        if (this.delayTask != null) this.delayTask.cancel();
        if (this.task != null) this.task.cancel();
        if (disc == null) this.disc = SoundAPI.Disc.getRandom();
        this.songSeconds = this.disc.getLengthInSeconds();
        SoundAPI.playSong(this.profile.getPlayer(), this.disc);
        this.secondsPassed = 0;
        this.finished = false;
        this.task = TaskUtil.runTaskTimerAsynchronously(this::handleDiscPlayback, 0, 20);
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
