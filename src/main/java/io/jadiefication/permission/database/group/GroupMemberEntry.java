package io.jadiefication.permission.database.group;

import io.ebean.Model;
import io.jadiefication.permission.database.player.PlayerGroupEntry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "groups")
public class GroupMemberEntry extends Model {

    @Id
    private int id;
    private String group_name;

    @OneToMany(mappedBy = "group")
    private List<PlayerGroupEntry> players;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public List<PlayerGroupEntry> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerGroupEntry> players) {
        this.players = players;
    }
}
