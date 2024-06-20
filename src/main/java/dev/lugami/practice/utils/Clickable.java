// Decompiled with: CFR 0.152
// Class Version: 8
package dev.lugami.practice.utils;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class Clickable {
    private final List<TextComponent> components = new ArrayList<TextComponent>();
    private String hoverText;
    private String text = "";

    public Clickable(String msg) {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(CC.translate(msg)));
        this.components.add(message);
        this.text = msg;
    }

    public Clickable(String msg, String hoverMsg, String clickString) {
        this.add(msg, hoverMsg, clickString);
        this.text = msg;
        this.hoverText = hoverMsg;
    }

    public TextComponent add(String msg, String hoverMsg, String clickString) {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(CC.translate(msg)));
        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
        }
        if (clickString != null && !clickString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }
        this.components.add(message);
        this.text = msg;
        this.hoverText = hoverMsg;
        return message;
    }

    public TextComponent addNoTranslate(String msg, String hoverMsg, String clickString) {
        TextComponent message = new TextComponent(msg);
        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));
        }
        if (clickString != null && !clickString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }
        this.components.add(message);
        this.text = msg;
        this.hoverText = hoverMsg;
        return message;
    }

    public TextComponent add(String msg, String hoverMsg) {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(CC.translate(msg)));
        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
        }
        this.components.add(message);
        this.text = msg;
        this.hoverText = hoverMsg;
        return message;
    }

    public TextComponent add(String msg, String hoverMsg, String clickString, String suggestString) {
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(CC.translate(msg)));
        if (hoverMsg != null && !hoverMsg.equalsIgnoreCase("")) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
        }
        if (clickString != null && !clickString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }
        if (suggestString != null && !suggestString.equalsIgnoreCase("")) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestString));
        }
        this.components.add(message);
        this.text = msg;
        this.hoverText = hoverMsg;
        return message;
    }

    public void add(String message) {
        this.text = message;
        this.components.add(new TextComponent(TextComponent.fromLegacyText(CC.translate(message))));
    }

    public void sendToPlayer(Player player) {
        player.spigot().sendMessage(this.asComponents());
    }

    public TextComponent[] asComponents() {
        return this.components.toArray(new TextComponent[0]);
    }

    public Clickable() {
    }

    public List<TextComponent> getComponents() {
        return this.components;
    }

    public String getHoverText() {
        return this.hoverText;
    }

    public String getText() {
        return this.text;
    }
}
