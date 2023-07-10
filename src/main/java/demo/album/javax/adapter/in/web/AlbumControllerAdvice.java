package demo.album.javax.adapter.in.web;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static demo.shared.ApiError.fieldApiError;
import static demo.shared.ApiErrorResponse.unprocessableEntity;

@RestControllerAdvice
class AlbumControllerAdvice {

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<?> handle(ConstraintViolationException ex) {
        var apiErrors = ex.getConstraintViolations().stream()
                .map(c -> fieldApiError(c.getPropertyPath().toString(), c.getMessage(), c.getInvalidValue()))
                .toList();
        return unprocessableEntity(apiErrors, "Schema validation failure.");
    }
}
