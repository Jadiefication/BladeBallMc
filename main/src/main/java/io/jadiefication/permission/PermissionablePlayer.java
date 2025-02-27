package io.jadiefication.permission;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionablePlayer extends Player {

    private final Set<PermissionableGroup> groups = new HashSet<>();
    public int currencyAmount = 0;
    public int winAmount = 0;

    public PermissionablePlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
    }

    public void addPermission(Permissions permission) {
        PermissionHandler.addPermission(this.asPlayer(), permission);
    }

    public void addGroup(PermissionableGroup group) {
        this.groups.add(group);
    }

    public void removePermission(Permissions permission) {
        PermissionHandler.removePermission(this.asPlayer(), permission);
    }

    public void removeGroup(PermissionableGroup group) {
        this.groups.remove(group);
    }

    public boolean hasPermission(Permissions permission) {
        if (getPlayerPermissions() != null) {
            return getPlayerPermissions().contains(permission) || this.asPlayer().getPermissionLevel() == 4
                    || this.groups.stream().anyMatch(group -> PermissionHandler.getPermissions(group).contains(permission));
        } else return this.asPlayer().getPermissionLevel() == 4;
    }

    public Set<Permissions> getPlayerPermissions() {
        return PermissionHandler.getPermissions(this.asPlayer());
    }
}

