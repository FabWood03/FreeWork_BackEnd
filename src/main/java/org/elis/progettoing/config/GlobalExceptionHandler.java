package org.elis.progettoing.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.elis.progettoing.dto.ErrorDTO;
import org.elis.progettoing.exception.*;
import org.elis.progettoing.exception.auction.*;
import org.elis.progettoing.exception.entity.*;
import org.elis.progettoing.exception.storage.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that catches various exceptions thrown by the application
 * and maps them to appropriate HTTP responses with detailed error messages.
 * <p>
 * This class uses {@link ControllerAdvice} to handle exceptions globally for all controllers.
 * It provides specific exception handling for different types of errors, such as validation errors,
 * authentication issues, database-related errors, and server errors.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link EntityNotFoundException} (404 Not Found) exceptions.
     *
     * @param exception the exception to handle
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler({
            EntityNotFoundException.class,
            FileNotFoundException.class,
    })
    public ResponseEntity<ErrorDTO> handleNotFoundException(EntityNotFoundException exception, WebRequest webRequest) {
        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.NOT_FOUND, exception.getMessage(), ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles various bad request (400) exceptions such as validation and data conversion errors.
     *
     * @param exception the exception to handle
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler({
            InvalidCredentialsException.class,
            InvalidEntityDataException.class,
            HttpMessageConversionException.class,
            TicketActionException.class,
            AuctionStatusUpdateException.class
    })
    public ResponseEntity<ErrorDTO> handleBadRequestException(Exception exception, WebRequest webRequest) {
        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.BAD_REQUEST, exception.getMessage(), ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors (400) and provides details for each field error.
     *
     * @param exception the validation exception containing field errors
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response with field validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException exception, WebRequest webRequest) {
        Map<String, String> errorMap = new HashMap<>();

        // Add field-specific errors
        exception.getBindingResult().getFieldErrors().forEach(
                error -> errorMap.put(error.getField(), error.getDefaultMessage())
        );

        // Add global errors (e.g., validation on class level)
        exception.getBindingResult().getGlobalErrors().forEach(
                error -> errorMap.put(error.getObjectName(), error.getDefaultMessage())
        );

        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.BAD_REQUEST, errorMap, ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link EntityAlreadyExistsException}, and other conflicts (409).
     *
     * @param exception the exception to handle
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler({
            EntityAlreadyExistsException.class,
            InvalidAuctionStateException.class,
            DirectoryNotEmptyException.class
    })
    public ResponseEntity<ErrorDTO> handleConflictException(EntityAlreadyExistsException exception, WebRequest webRequest) {
        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.CONFLICT, exception.getMessage(), ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.CONFLICT);
    }

    /**
     * Handles {@link AccessDeniedException}, {@link AuthenticationException}, and other access/authorization errors (403).
     *
     * @param exception the exception to handle
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler({
            UserNotActiveException.class,
            AccessDeniedException.class,
            AuthenticationException.class,
            AuctionOwnershipException.class,
            UserSubscriptionException.class,
    })
    public ResponseEntity<ErrorDTO> handleForbiddenException(RuntimeException exception, WebRequest webRequest) {
        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.FORBIDDEN, exception.getMessage(), ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles JWT-related exceptions such as {@link ExpiredJwtException} and {@link SignatureException} (401 Unauthorized).
     *
     * @param exception the exception to handle
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler({
            ExpiredJwtException.class,
            SignatureException.class
    })
    public ResponseEntity<ErrorDTO> handleUnauthorizedException(RuntimeException exception, WebRequest webRequest) {
        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.UNAUTHORIZED, exception.getMessage(), ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles SQL exceptions (500 Internal Server Error).
     *
     * @param exception the exception to handle
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorDTO> handleSQLException(SQLException exception, WebRequest webRequest) {
        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles server-side errors (500 Internal Server Error) related to entity creation, deletion, and other server failures.
     *
     * @param exception the exception to handle
     * @param webRequest the web request that triggered the exception
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler({
            EntityCreationException.class,
            EntityDeletionException.class,
            EntityEditException.class,
            ActiveUserEditException.class,
            EmailSendingException.class,
            AuctionException.class,
            FileDeletionException.class,
            FileNameGenerationException.class,
            StorageInitializationException.class
    })
    public ResponseEntity<ErrorDTO> handleServerError(RuntimeException exception, WebRequest webRequest) {
        ErrorDTO errorDTO = buildErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Utility method to build an {@link ErrorDTO} with the provided details.
     *
     * @param httpStatus the HTTP status code
     * @param message the error message or details
     * @param path the request URI that caused the exception
     * @return a new {@link ErrorDTO} with the given error details
     */
    private ErrorDTO buildErrorDTO(HttpStatus httpStatus, Object message, String path) {
        return new ErrorDTO(LocalDateTime.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path);
    }
}
