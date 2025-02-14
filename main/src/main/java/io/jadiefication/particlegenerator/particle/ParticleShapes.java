package io.jadiefication.particlegenerator.particle;

import io.jadiefication.particlegenerator.ParticleGenerator;
import net.jadiefication.string.StringExtender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;

import java.util.List;
import java.util.Map;

public enum ParticleShapes {

    SPHERE(true) {
        @Override
        public Task generate3D(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration) {
            return ParticleGenerator.spawnSphereParticles(instance, center, radiusX, radiusY, radiusZ, particle, duration);
        }

        @Override
        public Task generate2D(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) throws UnsupportedOperationException {
            throw new ShapeException(this.isThree, this.name());
        }

        public Task generate3D(Pos center, double radiusX, double radiusY, double radiusZ, Map<Particle, List<Player>> players, double duration) {
            return ParticleGenerator.spawnSphereParticles(center, radiusX, radiusY, radiusZ, players, duration);
        }
    },
    OCTAGON(true) {
        @Override
        public Task generate3D(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration) {
            throw new ShapeException(this.isThree, this.name());
        }

        @Override
        public Task generate2D(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
            return ParticleGenerator.spawnOctagonParticles(instance, center, radiusX, radiusY, particle, duration);
        }
    },
    SQUARE(true) {
        @Override
        public Task generate3D(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration) {
            throw new ShapeException(this.isThree, this.name());
        }

        @Override
        public Task generate2D(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
            return ParticleGenerator.spawnSquareParticles(instance, center, radiusX, radiusY, particle, duration);
        }
    },
    CIRCLE(true) {
        @Override
        public Task generate3D(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration) {
            throw new ShapeException(this.isThree, this.name());
        }

        @Override
        public Task generate2D(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
            return ParticleGenerator.spawnCircleParticles(instance, center, radiusX, radiusY, particle, duration);
        }
    };

    public final boolean isThree;

    ParticleShapes(boolean _isThree) {
        this.isThree = _isThree;
    }

    public abstract Task generate3D(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration);
    public abstract Task generate2D(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration);
}
