package edu.awieclawski.app.config;

import edu.awieclawski.app.dto.ErrorResponseDto;
import edu.awieclawski.app.exception.*;
import edu.awieclawski.app.facade.ErrorMessageFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

/**
 * https://medium.com/@AlexanderObregon/spring-boot-global-exception-handling-with-restcontrolleradvice-676c5b0b74ea
 * <p>
 * https://www.bezkoder.com/spring-boot-restcontrolleradvice/
 * <p>
 * https://yellowgnu.com/handling-missing-content-type-header-in-spring-boot-rest-apis/
 *
 * https://www.baeldung.com/spring-security-exceptions
 */
@RestControllerAdvice
@Order(1)
@RequiredArgsConstructor
@DependsOn(ErrorMessageFacade.BEAN_NAME)
public class GlobalRestExceptionHandler {

    private final ErrorMessageFacade errorMessageFacade;

    //

    @ExceptionHandler({
            HttpClientErrorException.BadRequest.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ErrorResponseDto handleHttpClientBadRequest(HttpClientErrorException.BadRequest ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler({
            HttpClientErrorException.Unauthorized.class
    })
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    ErrorResponseDto handleHttpClientUnauthorized(HttpClientErrorException.Unauthorized ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
    }

    @ExceptionHandler({
            HttpClientErrorException.Forbidden.class
    })
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    ErrorResponseDto handleHttpClientForbidden(HttpClientErrorException.Forbidden ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.FORBIDDEN, ex.getMessage(), ex);
    }

    @ExceptionHandler({
            HttpServerErrorException.InternalServerError.class
    })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleInternalServerError(HttpServerErrorException.InternalServerError ex) {
        return getErrorResponseDto(null, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            ResourceAccessException.class
    })
    @ResponseStatus(value = HttpStatus.CONFLICT)
    ErrorResponseDto handleConflict(RuntimeException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    //

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(SaveEntityException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponseDto handleSaveEntityException(SaveEntityException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(UpdateEntityException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponseDto handleUpdateEntityException(UpdateEntityException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(DeleteEntityException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponseDto handleDeleteEntityException(DeleteEntityException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMissingRequestHeaderException(MissingRequestHeaderException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(RequestHeaderValueException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleRequestHeaderValueException(RequestHeaderValueException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMissingPathVariableException(MissingPathVariableException ex, WebRequest request) {
        return getErrorResponseDto(request, HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    private ErrorResponseDto getErrorResponseDto(WebRequest webRequest, HttpStatus httpStatus, String message, Exception ex) {
        return errorMessageFacade.createMessage(message, httpStatus, webRequest, ex);
    }
}
