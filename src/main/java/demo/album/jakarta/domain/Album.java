package demo.album.jakarta.domain;

import lombok.Builder;
import demo.shared.Guards;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record Album(UUID id,
                    String title,
                    String artist,
                    BigDecimal price) {

    public Album {
        Guards.notNull(id, "id cannot be null.");
        Guards.notBlank(title, "title cannot be blank.");
        Guards.notBlank(artist, "artist cannot be blank.");
        Guards.notNull(price, "price cannot be null.");
        Guards.positive(price, "price must be greater than 0.");
    }
}
