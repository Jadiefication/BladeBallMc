package io.jadiefication.worldgen;

public class WorldNotFoundException extends RuntimeException {
    public WorldNotFoundException() {
        super("No world found in the desired directory.");
    }
}
