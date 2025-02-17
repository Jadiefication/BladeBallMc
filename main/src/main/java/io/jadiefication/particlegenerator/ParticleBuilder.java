package io.jadiefication.particlegenerator;

import io.jadiefication.particlegenerator.packets.PacketReceiver;
import io.jadiefication.particlegenerator.particle.ParticleShapes;
import io.jadiefication.particlegenerator.particle.ShapeException;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.timer.Task;

public abstract class ParticleBuilder {

    public static Task build(ParticleShapes shape, Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) throws ShapeException {
        return shape.generate2D(center, radiusX, radiusY, receiver, duration);
    }

    public static Task build(ParticleShapes shape, Pos center, double radiusX, double radiusY, double radiusZ, PacketReceiver receiver, double duration) throws ShapeException {
        return shape.generate3D(center, radiusX, radiusY, radiusZ, receiver, duration);
    }
}
