package demo.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static demo.shared.ApiErrorResponse.*;

@RestControllerAdvice
@Slf4j
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                          HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return badRequest("Malformed JSON request");
    }

    @Override
    protected ResponseEntity handleTypeMismatch(TypeMismatchException ex,
                                                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return badRequest("Type mismatch request");
    }

    @Override
    protected ResponseEntity handleNoHandlerFoundException(NoHandlerFoundException exception,
                                                           HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return notFound("Resource '" + exception.getRequestURL() + "' not found");
    }

    @Override
    protected ResponseEntity handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                 HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        var supportedMethods = exception.getSupportedHttpMethods();

        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }

        return methodNotAllowed(headers, "Request method '" + exception.getMethod() + "' not supported");
    }

    @Override
    protected ResponseEntity handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException exception,
                                                              HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return notAcceptable("Could not find acceptable representation");
    }

    @Override
    protected ResponseEntity handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception,
                                                             HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        var mediaTypes = exception.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }

        return unsupportedMediaType(headers, "Content type '" + exception.getContentType() + "' not supported");
    }

    @ExceptionHandler(Throwable.class)
    ResponseEntity<ApiErrorResponse> handleThrowable(Throwable throwable) {
        log.error("Request handling failed", throwable);
        return internalServerError("An unexpected error occurred");
    }
}
