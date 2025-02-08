package net.jadiefication.map;

public class ValueTypeException extends RuntimeException {

    public ValueTypeException() {
        super("The value is of not the specified type");
    }
}
