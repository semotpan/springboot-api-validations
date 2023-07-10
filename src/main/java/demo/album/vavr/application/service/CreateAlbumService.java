package demo.album.vavr.application.service;

import demo.album.vavr.application.port.in.CreateAlbumUseCase;
import demo.album.vavr.domain.Album;
import demo.shared.Failure;
import demo.shared.FieldViolation;
import io.micrometer.common.util.StringUtils;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

import static demo.shared.Failure.validatonFailure;
import static io.vavr.API.Invalid;
import static io.vavr.API.Valid;
import static java.util.Objects.isNull;

@Service("vCreateAlbumUseCase")
class CreateAlbumService implements CreateAlbumUseCase {

    static final String VALIDATION_FAILURE_MESSAGE = "Schema validation failure.";

    private final CreateAlbumCommandValidator validator = new CreateAlbumCommandValidator();

    @Override
    public Either<Failure, UUID> create(CreateAlbumCommand command) {
        // TODO: validate input schema
        var validation = validator.validate(command);
        if (validation.isInvalid())
            return Either.left(validatonFailure(VALIDATION_FAILURE_MESSAGE, validation.getError().toJavaList()));

        // TODO: validate business rules
//        if (!uniqueTitle(command.title()))
//            return Either.left(conflictFailure("title '%s' already exists.".formatted(command.title())));

        // TODO: manipulate model state
        var album = Album.builder()
                .id(UUID.randomUUID())
                .title(command.title())
                .artist(command.artist())
                .price(command.price())
                .build();

        // TODO: return output
        return Either.right(album.id());
    }


    private static final class CreateAlbumCommandValidator {

        Validation<Seq<FieldViolation>, CreateAlbumCommand> validate(CreateAlbumCommand command) {
            return Validation.combine(
                    validateTitle(command.title()),
                    validateArtist(command.artist()),
                    validatePrice(command.price())
            ).ap((title, artist, price) -> command);
        }

        private Validation<FieldViolation, String> validateTitle(String title) {
            if (StringUtils.isBlank(title))
                return Invalid(FieldViolation.builder()
                        .field(CreateAlbumCommand.FIELD_TITLE)
                        .message("title cannot be blank.")
                        .rejValue(title)
                        .build());

            return Valid(title);
        }

        private Validation<FieldViolation, String> validateArtist(String artist) {
            if (StringUtils.isBlank(artist))
                return Invalid(FieldViolation.builder()
                        .field(CreateAlbumCommand.FIELD_ARTIST)
                        .message("artist cannot be blank.")
                        .rejValue(artist)
                        .build());

            return Valid(artist);
        }

        private Validation<FieldViolation, BigDecimal> validatePrice(BigDecimal price) {
            if (isNull(price))
                return Invalid(FieldViolation.builder()
                        .field(CreateAlbumCommand.FIELD_PRICE)
                        .message("price cannot be null.")
                        .rejValue(price)
                        .build());

            if (price.compareTo(BigDecimal.ZERO) <= 0)
                return Invalid(FieldViolation.builder()
                        .field(CreateAlbumCommand.FIELD_PRICE)
                        .message("price must be greater than 0.")
                        .rejValue(price)
                        .build());

            return Valid(price);
        }
    }
}
