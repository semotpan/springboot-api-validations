package demo.shared;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public interface Failure {

    String message();

    Collection<FieldViolation> fieldViolations();

    static Failure validatonFailure(String message, Collection<FieldViolation> fieldViolations) {
        return new ValidationFailure(message, fieldViolations);
    }

    static Failure notFoundFailure(String message) {
        return new NotFoundFailure(message);
    }

    static Failure conflictFailure(String message) {
        return new ConflictFailure(message);
    }

    record ValidationFailure(String message,
                             Collection<FieldViolation> fieldViolations) implements Failure {

        public ValidationFailure {
            requireNonNull(fieldViolations, "fieldViolations cannot be null");
        }
    }

    record NotFoundFailure(String message) implements Failure {

        public Collection<FieldViolation> fieldViolations() {
            throw new UnsupportedOperationException();
        }
    }

    record ConflictFailure(String message) implements Failure {

        public Collection<FieldViolation> fieldViolations() {
            throw new UnsupportedOperationException();
        }
    }
}
