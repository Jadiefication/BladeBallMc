package io.jadiefication.permission.database.player;


import io.ebean.Finder;
import io.ebean.Model;
import io.jadiefication.permission.Permission;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "player_permissions")
public class PlayerPermissionEntry extends Model {

    @Id
    private int id;

    @Column(unique = true)
    private String uuid;

    private Permission permission;

    @OneToMany(mappedBy = "player")
    private List<PlayerGroupEntry> groups;

    public List<PlayerGroupEntry> getGroups() {
        return groups;
    }

    public void setGroups(List<PlayerGroupEntry> groups) {
        this.groups = groups;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
