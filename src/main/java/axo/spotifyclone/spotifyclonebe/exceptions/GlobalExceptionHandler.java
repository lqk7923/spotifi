package axo.spotifyclone.spotifyclonebe.exceptions;

import axo.spotifyclone.spotifyclonebe.dto.response.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(
            Exception exception
    ){
        return new ErrorMessage(10000, exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleResourceNotFound(
            ResourceNotFoundException exception
    ){
        return new ErrorMessage(10100, exception.getMessage());
    }
}
