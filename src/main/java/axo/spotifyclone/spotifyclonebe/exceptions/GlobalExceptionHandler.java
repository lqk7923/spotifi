package axo.spotifyclone.spotifyclonebe.exceptions;

import axo.spotifyclone.spotifyclonebe.dto.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("RESOURCE_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException exception){
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("BAD_REQUEST", exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception){
        log.error("Unexpected request failure", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred."));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception exception,
            Object body,
            @NonNull HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ){
        log.warn("Request failed (original status: {}): {}",
                statusCode.value(), request.getDescription(false), exception);
        return super.handleExceptionInternal(exception,
                new ApiErrorResponse("BAD_REQUEST", "Invalid request"), headers, HttpStatus.BAD_REQUEST, request);
    }
}
