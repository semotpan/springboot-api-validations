package demo.shared;

import lombok.Builder;

@Builder
public record FieldViolation(String field,
                             String message,
                             Object rejValue) {
}
