package src.main.permission;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PermissionablePlayer extends Player {

    private final List<PermissionableGroup> groups = new ArrayList<>();

    public PermissionablePlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
    }

    public void addPermission(Permission permission) {
        PermissionHandler.addPermission(this.asPlayer(), permission);
    }

    public void addGroup(PermissionableGroup group) {
        this.groups.add(group);
    }

    public void removePermission(Permission permission) {
        PermissionHandler.removePermission(this.asPlayer(), permission);
    }

    public void removeGroup(PermissionableGroup group) {
        this.groups.remove(group);
    }

    public boolean hasPermission(Permission permission) {
        if (getPlayerPermissions().contains(permission) || this.asPlayer().getPermissionLevel() == 4
                || this.groups.stream().anyMatch(group -> PermissionHandler.getPermissions(group).contains(permission))) {
            return true;
        } else {
            return false;
        }
    }

    public List<Permission> getPlayerPermissions() {
        return PermissionHandler.getPermissions(this.asPlayer());
    }
}

