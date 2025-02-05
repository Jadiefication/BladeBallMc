package io.jadiefication.particlegenerator;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;

public enum ThreeDimenstionalParticleShapes {

    SPHERE {
        @Override
        public void apply(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration) {
            ParticleGenerator.spawnSphereParticles(instance, center, radiusX, radiusY, radiusZ, particle, duration);
        }
    };

    public abstract void apply(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration);
}
