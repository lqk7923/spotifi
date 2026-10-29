package axo.spotifyclone.spotifyclonebe.exceptions;

import axo.spotifyclone.spotifyclonebe.dto.response.ApiErrorResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
                .body(new ApiErrorResponse(new ErrorMessage("RESOURCE_NOT_FOUND", exception.getMessage())));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException exception){
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(new ErrorMessage("BAD_REQUEST", exception.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception){
        log.error("Unexpected request failure", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(new ErrorMessage("INTERNAL_SERVER_ERROR", "An unexpected error occurred.")));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ){
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String code = status == null ? "HTTP_ERROR" : status.name();
        String message = status == null ? "The request could not be processed." : status.getReasonPhrase();
        if(exception instanceof TypeMismatchException){
            message = "A request parameter has an invalid value.";
        }else if(exception instanceof MissingServletRequestParameterException){
            message = "A required request parameter is missing.";
        }
        if(statusCode.is5xxServerError()){
            log.error("Request processing failed", exception);
            message = "An unexpected error occurred.";
        }
        return super.handleExceptionInternal(exception,
                new ApiErrorResponse(new ErrorMessage(code, message)), headers, statusCode, request);
    }
}
