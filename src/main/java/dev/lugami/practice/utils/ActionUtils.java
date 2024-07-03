package dev.lugami.practice.utils;

import dev.lugami.practice.Language;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ActionUtils {

    public static final Action UNFINISHED = player -> player.sendMessage(Language.UNFINISHED.format());

}
