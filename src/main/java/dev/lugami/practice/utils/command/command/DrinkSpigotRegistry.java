package dev.lugami.practice.utils.command.command;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import dev.lugami.practice.utils.command.exception.CommandRegistrationException;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DrinkSpigotRegistry {

    private final DrinkCommandService commandService;
    private final CommandMap commandMap;

    public DrinkSpigotRegistry(DrinkCommandService commandService) {
        this.commandService = commandService;
        commandMap = MinecraftServer.getServer().server.getCommandMap();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Command> getKnownCommands() throws NoSuchFieldException, IllegalAccessException {
        Object map = getPrivateField(commandMap);
        return (HashMap<String, Command>) map;
    }

    private Object getPrivateField(Object object) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final String v = Bukkit.getVersion();
        Class<?> clazz = object.getClass();
        Field objectField = "knownCommands".equals("commandMap") ? clazz.getDeclaredField("knownCommands") : "knownCommands".equals("knownCommands") ? v.contains("1.13.1") ? clazz.getSuperclass().getDeclaredField("knownCommands") : clazz.getDeclaredField("knownCommands") : null;
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }


    public void register(@Nonnull DrinkCommandContainer container, boolean unregisterExisting) throws CommandRegistrationException {
        if (unregisterExisting) {
            try {
                Map<String, Command> knownCommands = getKnownCommands();
                if (knownCommands.containsKey(container.getName().toLowerCase())) {
                    knownCommands.remove(container.getName().toLowerCase()).unregister(commandMap);
                }
                for (String s : container.getDrinkAliases()) {
                    if (knownCommands.containsKey(s.toLowerCase())) {
                        knownCommands.remove(s).unregister(commandMap);
                    }
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new CommandRegistrationException("Couldn't access knownCommands field in Bukkit CommandMap to unregister existing command(s)");
            }
        }

        commandMap.register(container.getCommandService().getPlugin().getName(), container);
    }



}
