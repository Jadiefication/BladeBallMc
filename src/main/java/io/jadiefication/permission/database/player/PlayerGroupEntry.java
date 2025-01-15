package io.jadiefication.permission.database.player;

import io.ebean.Model;
import io.jadiefication.permission.database.group.GroupMemberEntry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "player_groups")
public class PlayerGroupEntry extends Model {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "player_uuid", referencedColumnName = "uuid")
    private PlayerPermissionEntry player;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private GroupMemberEntry group;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlayerPermissionEntry getPlayer() {
        return player;
    }

    public void setPlayer(PlayerPermissionEntry player) {
        this.player = player;
    }

    public GroupMemberEntry getGroup() {
        return group;
    }

    public void setGroup(GroupMemberEntry group) {
        this.group = group;
    }
}
