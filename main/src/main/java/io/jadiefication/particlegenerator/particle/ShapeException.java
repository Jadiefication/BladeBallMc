package io.jadiefication.particlegenerator.particle;

import net.jadiefication.string.StringExtender;

public class ShapeException extends RuntimeException {

    public ShapeException(boolean isThree, String name) {
        super(StringExtender.capitalize(name) + " is not a " + (isThree ? "2D" : "3D") + " shape.");
    }
}
