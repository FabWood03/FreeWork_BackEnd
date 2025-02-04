package org.elis.progettoing.exception.entity;

/**
 * Custom exception to report invalid data of a specific entity.
 * Can be used in situations where an error occurs on an entity's data,
 * with differentiated constructors to include details about the problem property and value.
 */
public class InvalidEntityDataException extends RuntimeException {

    public InvalidEntityDataException(String entityName, String propertyName, long value, String message) {
        super(String.format("Dati non validi per %s con %s = %d. %s", entityName, propertyName, value, message));
    }

    public InvalidEntityDataException(String entityName, String propertyName, String value, String message) {
        super(String.format("Dati non validi per %s con %s = %s. %s", entityName, propertyName, value, message));
    }

    public InvalidEntityDataException(String invalidData) {
        super(invalidData);
    }
}
