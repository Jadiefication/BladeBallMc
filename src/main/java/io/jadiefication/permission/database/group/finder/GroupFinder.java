package io.jadiefication.permission.database.group.finder;

import io.ebean.Finder;
import io.jadiefication.permission.database.group.GroupPermissionEntry;

public class GroupFinder extends Finder<Integer, GroupPermissionEntry> {

    public GroupFinder() {
        super(GroupPermissionEntry.class);
    }

}
