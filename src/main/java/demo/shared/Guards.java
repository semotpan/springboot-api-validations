package demo.shared;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Guards {

    public static void notNull(Object object, String message) {
        if (object == null)
            throw new NullPointerException(message);
    }

    public static void notBlank(String text, String message) {
        if (isBlank(text))
            throw new IllegalArgumentException(message);
    }

    public static void positive(BigDecimal value, String message) {
        if (value.compareTo(ZERO) <= 0)
            throw new IllegalArgumentException(message);
    }
}
