package org.elis.progettoing.exception.entity;

/**
 * Custom exception to report errors due to missing entities.
 * Can be used to report errors due to entities not found.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String objectName, String propertyName, String value) {
        super(String.format("Nessun %s con %s = %s è stato trovato.", objectName, propertyName, value));
    }

    public EntityNotFoundException(String objectName, String propertyName, long value) {
        super(String.format("Nessun %s con %s = %d è stato trovato.", objectName, propertyName, value));
    }
}
