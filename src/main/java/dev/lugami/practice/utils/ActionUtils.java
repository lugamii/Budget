package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ActionUtils {

    public static final Action UNFINISHED = player -> player.sendMessage(CC.translate("&cThis feature is not ready yet."));

}
