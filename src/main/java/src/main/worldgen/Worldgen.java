package src.main.worldgen;

import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.block.Block;

public abstract class Worldgen {

    public static void generateWorld(InstanceContainer container) {

        JNoise noise = JNoise.newBuilder()
                .fastSimplex(FastSimplexNoiseGenerator.newBuilder().build())
                .scale(0.005)
                .build();

        // Define your world boundaries
        int minX = -500;  // Example values
        int maxX = 500;
        int minZ = -500;
        int maxZ = 500;

        container.setGenerator(unit -> {
            Point start = unit.absoluteStart();
            for (int x = 0; x < unit.size().x(); x++) {
                for (int z = 0; z < unit.size().z(); z++) {
                    Point bottom = start.add(x, 0, z);

                    // Check if within bounds
                    if (bottom.x() >= minX && bottom.x() <= maxX &&
                            bottom.z() >= minZ && bottom.z() <= maxZ) {
                        synchronized (noise) {
                            double height = noise.evaluateNoise(bottom.x(), bottom.z()) * 16;
                            unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(height), Block.STONE);
                        }
                    }
                }
            }
        });
        container.setWorldBorder(new WorldBorder(1002.0, 0.0, 0.0, 48, 0));
    }
}
