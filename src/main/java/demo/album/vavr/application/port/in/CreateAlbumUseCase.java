package demo.album.vavr.application.port.in;

import io.vavr.control.Either;
import lombok.Builder;
import demo.shared.Failure;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateAlbumUseCase {

    Either<Failure, UUID> create(CreateAlbumCommand command);

    @Builder
    record CreateAlbumCommand(String title,
                              String artist,
                              BigDecimal price) {

        public static final String FIELD_TITLE = "title";
        public static final String FIELD_ARTIST = "artist";
        public static final String FIELD_PRICE = "price";

    }
}
