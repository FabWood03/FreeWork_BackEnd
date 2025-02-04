package org.elis.progettoing.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.utils.customConverter.ObjectConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ObjectConverterTest {

    private ObjectConverter objectConverter;

    private String testJsonString;
    private Map<String, Object> testValue;

    @BeforeEach
    void setUp() {
        // Inizializza ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Crea l'oggetto ObjectConverter con l'ObjectMapper
        objectConverter = new ObjectConverter(objectMapper);

        // Crea un oggetto di esempio
        testValue = new HashMap<>();
        testValue.put("name", "test");
        testValue.put("value", 123);

        // La stringa JSON prevista
        testJsonString = "{\"name\":\"test\",\"value\":123}";
    }

    @Test
    void testConvertToDatabaseColumn() {
        // Act: Converte l'oggetto in una stringa JSON
        String result = objectConverter.convertToDatabaseColumn(testValue);

        // Assert: Verifica che la stringa risultante corrisponda alla stringa JSON
        Assertions.assertEquals(testJsonString, result);
    }

    @Test
    void testConvertToDatabaseColumnWithNull() {
        // Act: Converte un oggetto null in una stringa JSON
        String result = objectConverter.convertToDatabaseColumn(null);

        // Assert: Verifica che il risultato sia "{}" quando l'oggetto è null
        Assertions.assertNull(result);
    }

    @Test
    void testConvertToEntityAttribute() {
        // Act: Converte una stringa JSON in un oggetto
        Object result = objectConverter.convertToEntityAttribute(testJsonString);

        // Assert: Verifica che l'oggetto deserializzato non sia null
        Assertions.assertNotNull(result);

        // Verifica che il risultato sia una mappa con i dati attesi
        Assertions.assertInstanceOf(Map.class, result);
        Map<?, ?> resultMap = (Map<?, ?>) result;
        Assertions.assertEquals("test", resultMap.get("name"));
        Assertions.assertEquals(123, resultMap.get("value"));
    }

    @Test
    void testConvertToEntityAttributeWithInvalidJson() {
        // Crea un JSON invalido
        String invalidJson = "{name: test, value:}";

        // Act & Assert: Verifica che venga sollevata un'eccezione durante la deserializzazione
        Assertions.assertThrows(RuntimeException.class, () -> objectConverter.convertToEntityAttribute(invalidJson));
    }

    @Test
    void testConvertToDatabaseColumnWithException() throws Exception {
        // Mock del comportamento dell'ObjectMapper per sollevare un'eccezione
        ObjectMapper mockObjectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mockObjectMapper.writeValueAsString(Mockito.any()))
                .thenThrow(new JsonProcessingException("Test exception") {});

        // Crea un nuovo ObjectConverter con il mock
        ObjectConverter mockObjectConverter = new ObjectConverter(mockObjectMapper);

        // Act & Assert: Verifica che venga sollevata un'eccezione durante la serializzazione
        Assertions.assertThrows(RuntimeException.class, () -> mockObjectConverter.convertToDatabaseColumn(testValue));
    }

    @Test
    void testConvertToEntityAttributeWithException() throws Exception {
        // Mock del comportamento dell'ObjectMapper per sollevare un'eccezione
        ObjectMapper mockObjectMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mockObjectMapper.readValue(Mockito.anyString(), Mockito.eq(Object.class)))
                .thenThrow(new JsonProcessingException("Test exception") {});

        // Crea un nuovo ObjectConverter con il mock
        ObjectConverter mockObjectConverter = new ObjectConverter(mockObjectMapper);

        // Act & Assert: Verifica che venga sollevata un'eccezione durante la deserializzazione
        Assertions.assertThrows(RuntimeException.class, () -> mockObjectConverter.convertToEntityAttribute(testJsonString));
    }
}
