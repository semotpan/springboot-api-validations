package demo.shared;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collection;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.ResponseEntity.status;

@ToString
@Getter
public final class ApiErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final HttpStatus errorCode;
    private final String message;
    private final Collection<ApiError> errors;

    @Builder
    private ApiErrorResponse(HttpStatus httpStatus, String message, Collection<ApiError> errors) {
        this.timestamp = Instant.now();
        this.status = httpStatus.value();
        this.errorCode = httpStatus;
        this.message = message;
        this.errors = isNull(errors) ? emptyList() : errors;
    }

    public static ResponseEntity<ApiErrorResponse> badRequest(String message) {
        return ResponseEntity.badRequest()
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ApiErrorResponse> notFound(String message) {
        return status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(NOT_FOUND)
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ApiErrorResponse> unprocessableEntity(Collection<ApiError> errors, String message) {
        return ResponseEntity.unprocessableEntity()
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(UNPROCESSABLE_ENTITY)
                        .message(message)
                        .errors(errors)
                        .build());
    }

    public static ResponseEntity<ApiErrorResponse> conflict(String message) {
        return status(CONFLICT)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(CONFLICT)
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ApiErrorResponse> methodNotAllowed(HttpHeaders headers, String message) {
        return status(METHOD_NOT_ALLOWED)
                .headers(headers)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(METHOD_NOT_ALLOWED)
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ApiErrorResponse> notAcceptable(String message) {
        return status(NOT_ACCEPTABLE)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(NOT_ACCEPTABLE)
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ApiErrorResponse> unsupportedMediaType(HttpHeaders headers, String message) {
        return status(UNSUPPORTED_MEDIA_TYPE)
                .headers(headers)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(UNSUPPORTED_MEDIA_TYPE)
                        .message(message)
                        .build());
    }

    public static ResponseEntity<ApiErrorResponse> internalServerError(String message) {
        return ResponseEntity.internalServerError()
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(INTERNAL_SERVER_ERROR)
                        .message(message)
                        .build());
    }
}
