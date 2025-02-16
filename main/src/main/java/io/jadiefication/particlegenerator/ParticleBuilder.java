package io.jadiefication.particlegenerator;

import io.jadiefication.particlegenerator.particle.ParticleShapes;
import io.jadiefication.particlegenerator.particle.ShapeException;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;

public abstract class ParticleBuilder {

    public static Task build(ParticleShapes shape, Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) throws ShapeException {
        return shape.generate2D(instance, center, radiusX, radiusY, particle, duration);
    }

    public static Task build(ParticleShapes shape, Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration) throws ShapeException {
        return shape.generate3D(instance, center, radiusX, radiusY, radiusZ, particle, duration);
    }
}
