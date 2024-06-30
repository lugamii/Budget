package dev.lugami.practice.utils.fake;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class FakePlayer extends CraftPlayer {

    public FakePlayer(EntityPlayer entity) {
        super((CraftServer) Bukkit.getServer(), entity);
    }

    public FakePlayer(CraftPlayer craftPlayer) {
        this(craftPlayer.getHandle());
    }

    public boolean teleport(Location loc) {
        FakePlayerUtils.updateFakePlayerPosition(this, loc);
        return super.teleport(loc);
    }

}
