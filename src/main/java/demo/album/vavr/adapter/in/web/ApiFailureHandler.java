package demo.album.vavr.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import demo.shared.ApiError;
import demo.shared.Failure;
import demo.shared.Failure.ConflictFailure;
import demo.shared.Failure.NotFoundFailure;
import demo.shared.Failure.ValidationFailure;
import demo.shared.FieldViolation;

import java.util.Collection;

import static demo.shared.ApiError.fieldApiError;
import static demo.shared.ApiErrorResponse.*;

@Component
final class ApiFailureHandler {

    private final ValidationFailureToApiMapper apiMapper = new ValidationFailureToApiMapper();

    ResponseEntity<?> handle(Failure failure) {
        return switch (failure) {
            case NotFoundFailure notFoundFailure -> notFound(notFoundFailure.message());
            case ConflictFailure conflictFailure -> conflict(conflictFailure.message());
            case ValidationFailure validFailure -> unprocessableEntity(apiMapper.map(failure.fieldViolations()), validFailure.message());
            default -> throw new IllegalArgumentException("Unsupported failure type");
        };
    }

    private static class ValidationFailureToApiMapper {

        private Collection<ApiError> map(Collection<FieldViolation> violations) {
            return violations.stream()
                    .map(violation -> fieldApiError(violation.field(), violation.message(), violation.rejValue()))
                    .toList();
        }
    }
}
