package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CC {

    public static final String CHAT_BAR = translate("&7&m" + StringUtils.repeat("-", 48));
    public static final String SCORE_BAR = translate("&7&m" + StringUtils.repeat("-", 22));

    /**
     * Translates color codes in a given string.
     * The method replaces all instances of '&' with '§', which is used by Minecraft for color codes.
     *
     * @param string the string to translate
     * @return the translated string with color codes
     */
    public String translate(String string) {
        StringBuilder translator = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (c == '&') c = '§';
            translator.append(c);
        }
        return translator.toString();
    }

    /**
     * Translates color codes in a given string list.
     * The method replaces all instances of '&' with '§', which is used by Minecraft for color codes.
     *
     * @param strings the list to translate
     * @return the translated string with color codes
     */
    public List<String> translate(List<String> strings) {
        List<String> list = new ArrayList<>();
        for (String string : strings) {
            list.add(CC.translate(string));
        }
        return list;
    }

}
