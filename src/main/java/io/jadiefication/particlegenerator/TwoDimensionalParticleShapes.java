package io.jadiefication.particlegenerator;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;

public enum TwoDimensionalParticleShapes {

    OCTAGON {
        @Override
        public void apply(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
            ParticleGenerator.spawnOctagonParticles(instance, center, radiusX, radiusY, particle, duration);
        }
    },
    SQUARE {
        @Override
        public void apply(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
            ParticleGenerator.spawnSquareParticles(instance, center, radiusX, radiusY, particle, duration);
        }
    };

    public abstract void apply(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration);
}
