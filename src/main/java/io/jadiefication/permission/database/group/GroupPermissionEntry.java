package io.jadiefication.permission.database.group;

import io.ebean.Model;
import io.jadiefication.permission.Permission;

import javax.persistence.*;

@Entity
@Table(name = "group_permissions")
public class GroupPermissionEntry extends Model {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private GroupMemberEntry group;


    private Permission permission;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public GroupMemberEntry getGroup() {
        return group;
    }

    public void setGroup(GroupMemberEntry group) {
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
