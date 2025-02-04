package org.elis.progettoing.exception.entity;

/**
 * Custom exception to report errors during entity creation.
 */
public class EntityCreationException extends RuntimeException {

    public EntityCreationException(String entityName, String propertyName, long value) {
        super(String.format("Si è verificato un errore durante la creazione di %s con %s = %d", entityName, propertyName, value));
    }

    public EntityCreationException(String entityName, String propertyName, String value) {
        super(String.format("Si è verificato un errore durante la creazione di %s con %s = %s", entityName, propertyName, value));
    }
}
