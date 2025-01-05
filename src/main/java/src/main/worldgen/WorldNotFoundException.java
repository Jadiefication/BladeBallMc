package src.main.worldgen;

public class WorldNotFoundException extends RuntimeException {
    public WorldNotFoundException() {
        super("No world found in the desired directory.");
    }
}
