package io.jadiefication.particlegenerator.particle;

import io.jadiefication.particlegenerator.ParticleGenerator;
import io.jadiefication.particlegenerator.packets.PacketReceiver;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.timer.Task;

public enum ParticleShapes {

    SPHERE(true) {
        @Override
        public Task generate3D(Pos center, double radiusX, double radiusY, double radiusZ, PacketReceiver receiver, double duration) {
            return ParticleGenerator.spawnSphereParticles(center, radiusX, radiusY, radiusZ, receiver, duration);
        }

        @Override
        public Task generate2D(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) throws UnsupportedOperationException {
            throw new ShapeException(this.isThree, this.name());
        }
    },
    OCTAGON(false) {
        @Override
        public Task generate3D(Pos center, double radiusX, double radiusY, double radiusZ, PacketReceiver receiver, double duration) {
            throw new ShapeException(this.isThree, this.name());
        }

        @Override
        public Task generate2D(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) {
            return ParticleGenerator.spawnOctagonParticles(center, radiusX, radiusY, receiver, duration);
        }
    },
    SQUARE(false) {
        @Override
        public Task generate3D(Pos center, double radiusX, double radiusY, double radiusZ, PacketReceiver receiver, double duration) {
            throw new ShapeException(this.isThree, this.name());
        }

        @Override
        public Task generate2D(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) {
            return ParticleGenerator.spawnSquareParticles(center, radiusX, radiusY, receiver, duration);
        }
    },
    CIRCLE(false) {
        @Override
        public Task generate3D(Pos center, double radiusX, double radiusY, double radiusZ, PacketReceiver receiver, double duration) {
            throw new ShapeException(this.isThree, this.name());
        }

        @Override
        public Task generate2D(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) {
            return ParticleGenerator.spawnCircleParticles(center, radiusX, radiusY, receiver, duration);
        }
    };

    public final boolean isThree;

    ParticleShapes(boolean _isThree) {
        this.isThree = _isThree;
    }

    public abstract Task generate3D(Pos center, double radiusX, double radiusY, double radiusZ, PacketReceiver receiver, double duration);
    public abstract Task generate2D(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration);
}
