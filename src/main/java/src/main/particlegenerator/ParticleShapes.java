package src.main.particlegenerator;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public enum ParticleShapes {

    OCTAGON {
        @Override
        public void apply(Instance instance, Pos center, double radiusX, double radiusY) {
            ParticleGenerator.spawnOctagonParticles(instance, center, radiusX, radiusY);
        }
    },
    SQUARE {
        @Override
        public void apply(Instance instance, Pos center, double radiusX, double radiusY) {
            ParticleGenerator.spawnSquareParticles(instance, center, radiusX, radiusY);
        }
    };

    public abstract void apply(Instance instance, Pos center, double radiusX, double radiusY);
}
