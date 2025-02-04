package org.elis.progettoing.utils.customConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

/**
 * A custom JPA AttributeConverter that serializes and deserializes Java Objects to and from JSON strings
 * for database storage. This converter uses Jackson's ObjectMapper for JSON processing.
 * <p>
 * This class is annotated with {@link Converter} to be recognized by JPA and {@link Component} to be managed
 * by Spring's dependency injection mechanism.
 */
@Converter
@Component
public class ObjectConverter implements AttributeConverter<Object, String> {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new ObjectConverter with the provided ObjectMapper.
     *
     * @param objectMapper The ObjectMapper instance used for JSON serialization and deserialization.
     */
    public ObjectConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Converts a Java Object to a JSON string for database storage.
     *
     * @param attribute The Java Object to convert. Can be null.
     * @return The JSON string representation of the Object, or null if the input is null.
     * @throws CustomConversionException If an error occurs during JSON serialization.
     */
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null; // Use null to represent missing values in the database.
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new CustomConversionException("Error converting Object to String", e);
        }
    }

    /**
     * Converts a JSON string from the database back to a Java Object.
     *
     * @param dbData The JSON string from the database. Can be null or empty.
     * @return The Java Object represented by the JSON string, or null if the input is null or empty.
     * @throws CustomConversionException If an error occurs during JSON deserialization.
     */
    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null; // Return null if the database contains empty values.
        }

        try {
            return objectMapper.readValue(dbData, Object.class);
        } catch (JsonProcessingException e) {
            throw new CustomConversionException("Error converting String to Object", e);
        }
    }

    /**
     * A custom RuntimeException used to wrap exceptions that occur during the conversion process.
     * This allows for more specific exception handling related to object conversion.
     */
    public static class CustomConversionException extends RuntimeException {
        /**
         * Constructs a new CustomConversionException with the specified message and cause.
         *
         * @param message The detail message.
         * @param cause   The cause of the exception.
         */
        public CustomConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}