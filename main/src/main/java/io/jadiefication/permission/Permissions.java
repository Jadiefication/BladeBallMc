package io.jadiefication.permission;

import java.util.ArrayList;
import java.util.List;

public record Permissions(String name) {

    private static final List<Permissions> PERMISSIONS = new ArrayList<>();

    public Permissions(String name) {
        this.name = name.toUpperCase();
        PERMISSIONS.add(this);
    }

    public static boolean hasPermission(String name) {
        return PERMISSIONS.contains(new Permissions(name));
    }
}
