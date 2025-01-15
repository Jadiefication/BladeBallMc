package io.jadiefication.permission.database.player.finder;

import io.ebean.Finder;
import io.jadiefication.permission.database.player.PlayerPermissionEntry;

public class PlayerFinder extends Finder<Integer, PlayerPermissionEntry> {

    public PlayerFinder() {
        super(PlayerPermissionEntry.class);
    }

    public PlayerPermissionEntry findByUUID(String uuid) {
        return query().where()
                .eq("uuid", uuid)
                .findOne();
    }

    public PlayerPermissionEntry findById(int id) {
        return query().where()
                .eq("id", id)
                .findOne();
    }
}
