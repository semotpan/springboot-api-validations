package demo.album.jakarta.application.port.in;

import demo.shared.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateAlbumUseCase {

    UUID add(CreateAlbumCommand command);

    @Getter
    class CreateAlbumCommand extends SelfValidating<CreateAlbumCommand> {

        @NotBlank(message = "title cannot be blank.")
        private final String title;

        @NotBlank(message = "artist cannot be blank.")
        private final String artist;

        @NotNull(message = "price cannot be null.")
        @Positive(message = "price must be greater than 0.")
        private final BigDecimal price;

        @Builder
        public CreateAlbumCommand(String title, String artist, BigDecimal price) {
            this.title = title;
            this.artist = artist;
            this.price = price;
            this.validateSelf();
        }
    }
}
