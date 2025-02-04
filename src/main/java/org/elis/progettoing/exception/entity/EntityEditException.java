package org.elis.progettoing.exception.entity;

/**
 * Custom exception to report errors when editing entities.
 */
public class EntityEditException extends RuntimeException {

    public EntityEditException(String objectName, String propertyName, long value) {
        super(String.format("Si è verificato un errore nell'aggiornamento dell'entità %s con %s = %d.", propertyName, objectName, value));
    }
}

