package dev.lugami.practice;

import dev.lugami.practice.utils.CC;
import lombok.AllArgsConstructor;

import java.text.MessageFormat;

@AllArgsConstructor
public enum Language {

    NULL_TARGET("MESSAGES.ERRORS.NULL-TARGET"),
    CANNOT_DO_ACTION("MESSAGES.ERRORS.CANNOT-DO-ACTION"),
    UNFINISHED("MESSAGES.ERRORS.UNFINISHED"),;

    private final String path;

    public String format(String... toFormat) {
        return new MessageFormat(CC.translate(Budget.getInstance().getMessagesConfig().getString(path))).format(toFormat);
    }

}
