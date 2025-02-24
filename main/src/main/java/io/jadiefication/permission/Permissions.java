package io.jadiefication.permission;

import java.util.*;

public record Permissions(String name) {

    private static final Set<Permissions> PERMISSIONS = new HashSet<>();

    public Permissions(String name) {
        this.name = name.toUpperCase();
        PERMISSIONS.add(this);
    }

    public static void initialize() {
        PERMISSIONS.addAll(List.of(new Permissions("OP"),
                new Permissions("USE_CUSTOM_ITEM"),
                new Permissions("PLACE"),
                new Permissions("PARTICLE_SHAPE"),
                new Permissions("TIME"),
                new Permissions("WEATHER"),
                new Permissions("GAMEMODE"),
                new Permissions("STOP"),
                new Permissions("START"),
                new Permissions("BREAK")
                ));
    }

    public static boolean exists(String name) {
        return PERMISSIONS.contains(new Permissions(name));
    }

    public static Permissions getPermission(String name) throws NoSuchElementException {
        return PERMISSIONS.stream().filter(permissions -> permissions.name.equals(name.toUpperCase()))
                .findAny().get();
    }
}
