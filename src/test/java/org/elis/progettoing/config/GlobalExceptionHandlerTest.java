package org.elis.progettoing.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.elis.progettoing.dto.ErrorDTO;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.exception.entity.InvalidEntityDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;

    // Set up the test data before each test
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(ServletWebRequest.class);

        // Mock the HttpServletRequest
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/test-endpoint");

        // Ensure that getRequest() returns the mocked HttpServletRequest
        when(((ServletWebRequest) webRequest).getRequest()).thenReturn(mockRequest);
    }

    @Test
    void handleNotFoundException_ShouldReturn404() {
        // Test for EntityNotFoundException
        EntityNotFoundException exception = new EntityNotFoundException("entity", "ID", 1L);
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Nessun entity con ID = 1 è stato trovato.", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleBadRequestException_ShouldReturn400() {
        // Test for InvalidEntityDataException
        InvalidEntityDataException exception = new InvalidEntityDataException("Invalid data");
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleBadRequestException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleValidationException_ShouldReturn400WithFieldErrors() {
        // Mock MethodArgumentNotValidException and BindingResult
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        // Create field errors
        List<FieldError> fieldErrorList = new ArrayList<>();
        fieldErrorList.add(new FieldError("entity", "field1", "must not be null"));
        fieldErrorList.add(new FieldError("entity", "field2", "must be greater than 0"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrorList);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // Call the handler method
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleValidationException(exception, webRequest);

        // Validate the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleValidationException_ShouldReturn400WithGlobalErrors() {
        // Mock MethodArgumentNotValidException and BindingResult for global errors
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrorList = new ArrayList<>();
        fieldErrorList.add(new FieldError("entity", "field1", "must not be null"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrorList);

        List<ObjectError> globalErrorList = new ArrayList<>();
        globalErrorList.add(new ObjectError("entity", "Global validation failed"));
        when(bindingResult.getGlobalErrors()).thenReturn(globalErrorList);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleValidationException(exception, webRequest);

        // Validate the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleConflictException_ShouldReturn409() {
        // Test for EntityAlreadyExistsException
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("entity", "ID", 1L);
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleConflictException(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Esiste già un entity con ID = 1.", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleForbiddenException_ShouldReturn403() {
        // Test for AccessDeniedException
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleForbiddenException(exception, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleUnauthorizedException_ShouldReturn401() {
        // Mock ExpiredJwtException
        ExpiredJwtException exception = mock(ExpiredJwtException.class);
        when(exception.getMessage()).thenReturn("JWT token is expired");

        // Call the handler method
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleUnauthorizedException(exception, webRequest);

        // Validate the response
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleSQLException_ShouldReturn500() {
        // Test for SQLException
        SQLException exception = new SQLException("Database error");
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleSQLException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database error", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }

    @Test
    void handleServerError_ShouldReturn500() {
        // Test for EntityCreationException
        EntityCreationException exception = new EntityCreationException("entity", "ID", 1L);
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleServerError(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Si è verificato un errore durante la creazione di entity con ID = 1", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("/test-endpoint", response.getBody().getPath());
    }
}
