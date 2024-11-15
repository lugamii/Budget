package dev.lugami.practice;

import dev.lugami.practice.utils.CC;
import lombok.AllArgsConstructor;

import java.text.MessageFormat;

@AllArgsConstructor
public enum Language {

    NULL_TARGET("MESSAGES.ERRORS.NULL-TARGET"),
    CANNOT_DO_ACTION("MESSAGES.ERRORS.CANNOT-DO-ACTION"),
    UNFINISHED("MESSAGES.ERRORS.UNFINISHED"),
    CANNOT_DUEL_SELF("MESSAGES.ERRORS.CANNOT-DUEL-SELF"),
    TARGET_DUELS_DISABLED("MESSAGES.ERRORS.TARGET-DUELS-DISABLED"),
    TARGET_BUSY("MESSAGES.ERRORS.TARGET-BUSY"),
    SNAPSHOT_NOT_FOUND("MESSAGES.ERRORS.SNAPSHOT-NOT-FOUND"),

    ALREADY_IN_PARTY("MESSAGES.ERRORS.ALREADY-IN-PARTY"),
    NOT_IN_PARTY("MESSAGES.ERRORS.NOT-IN-PARTY"),
    NO_PARTY_INVITE("MESSAGES.ERRORS.NO-PARTY-INVITE"),
    CANNOT_INVITE_SELF("MESSAGES.ERRORS.CANNOT-INVITE-SELF"),
    CANNOT_KICK_SELF("MESSAGES.ERRORS.CANNOT-KICK-SELF"),
    NOT_LEADER("MESSAGES.ERRORS.NOT-LEADER"),
    DUEL_DECLINED("MESSAGES.DUELS.DECLINED"),
    NO_DUEL_REQUEST("MESSAGES.ERRORS.NO-DUEL-REQUEST"),
    CANNOT_SPECTATE_SELF("MESSAGES.ERRORS.CANNOT-SPECTATE-SELF"),;

    private final String path;

    public String format(String... toFormat) {
        if (toFormat.length == 0) {
            return CC.translate(Budget.getInstance().getLanguageConfig().getString(this.path));
        }
        return new MessageFormat(CC.translate(Budget.getInstance().getLanguageConfig().getString(this.path))).format(toFormat);
    }

}
