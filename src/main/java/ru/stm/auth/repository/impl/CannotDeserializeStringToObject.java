package ru.stm.auth.repository.impl;

/**
 * Created by KADmitriev on 6/28/2018.
 */
public class CannotDeserializeStringToObject extends RuntimeException {

    private final String stringToDeserialization;

    public CannotDeserializeStringToObject(String message, Throwable cause, String stringToDeserialization) {
        super(message, cause);
        this.stringToDeserialization = stringToDeserialization;
    }

    public CannotDeserializeStringToObject(Throwable cause, String stringToDeserialization) {
        super(cause);
        this.stringToDeserialization = stringToDeserialization;
    }

    public String getStringToDeserialization() {
        return stringToDeserialization;
    }
}
