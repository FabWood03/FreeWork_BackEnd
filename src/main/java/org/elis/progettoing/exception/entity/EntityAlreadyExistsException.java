package org.elis.progettoing.exception.entity;

/**
 * Custom exception to report errors due to the presence of existing entities.
 * Can be used to report duplicate entity errors.
 */
public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String entityName, String propertyName, String value){
        super(String.format("Esiste già un %s con %s = %s.", entityName, propertyName, value));
    }

    public EntityAlreadyExistsException(String entityName, String propertyName, long value){
        super(String.format("Esiste già un %s con %s = %d.", entityName, propertyName, value));
    }
}
