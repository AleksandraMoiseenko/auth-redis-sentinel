package ru.stm.auth.repository.impl;

/**
 * Created by KADmitriev on 6/29/2018.
 */
public class CannotSerializeObjectToString extends RuntimeException {

    private final Object objectToSerialization;

    public CannotSerializeObjectToString(String message, Throwable cause, Object objectToSerialization) {
        super(message, cause);
        this.objectToSerialization = objectToSerialization;
    }

    public CannotSerializeObjectToString(Throwable cause, Object objectToSerialization) {
        super(cause);
        this.objectToSerialization = objectToSerialization;
    }

    public Object getObjectToSerialization() {
        return objectToSerialization;
    }
}
