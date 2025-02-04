package org.elis.progettoing.exception.entity;

/**
 * Custom exception to report errors when deleting entities.
 */
public class EntityDeletionException extends RuntimeException {

    public EntityDeletionException(String objectName, String propertyName, long value) {
        super(String.format("Si è verificato un errore durante il tentativo di eliminare %s con %s = %d.", objectName, propertyName, value));
    }

    public EntityDeletionException(String objectName, String propertyName, String value) {
        super(String.format("Si è verificato un errore durante il tentativo di eliminare %s con %s = %s.", objectName, propertyName, value));
    }
}
