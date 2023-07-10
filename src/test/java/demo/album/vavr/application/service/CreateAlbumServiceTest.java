package demo.album.vavr.application.service;

import demo.shared.Failure;
import demo.shared.FieldViolation;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static demo.album.vavr.application.port.in.CreateAlbumUseCase.CreateAlbumCommand;
import static demo.album.vavr.application.port.in.CreateAlbumUseCase.CreateAlbumCommand.*;
import static demo.album.vavr.application.service.CreateAlbumService.VALIDATION_FAILURE_MESSAGE;
import static demo.shared.Failure.validatonFailure;

@Tag("unit")
class CreateAlbumServiceTest {

    CreateAlbumService service;

    @BeforeEach
    void setUp() {
        service = new CreateAlbumService();
    }

    @Test
    @DisplayName("Should create a new album successfully")
    void createNewAlbum() {
        // given
        var command = validAlbum().build();

        // when
        var either = service.create(command);

        // then
        VavrAssertions.assertThat(either)
                .isRight()
                .containsRightInstanceOf(UUID.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should fail album creation when 'title' is blank")
    void failWhenTitleIsBlank(String title) {
        // given
        var command = validAlbum().title(title).build();

        // when
        var either = service.create(command);

        // then
        VavrAssertions.assertThat(either)
                .isLeft()
                .containsOnLeft(failure(new FieldViolation(FIELD_TITLE, "title cannot be blank.", title)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should fail album creation when 'artist' is blank")
    void failWhenArtistIsBlank(String artist) {
        // given
        var command = validAlbum().artist(artist).build();

        // when
        var either = service.create(command);

        // then
        VavrAssertions.assertThat(either)
                .isLeft()
                .containsOnLeft(failure(new FieldViolation(FIELD_ARTIST, "artist cannot be blank.", artist)));
    }

    @Test
    @DisplayName("Should fail album creation when 'price' is null")
    void failWhenPriceIsNull() {
        // given
        var command = validAlbum().price(null).build();

        // when
        var either = service.create(command);

        // then
        VavrAssertions.assertThat(either)
                .isLeft()
                .containsOnLeft(failure(new FieldViolation(FIELD_PRICE, "price cannot be null.", null)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10, 0})
    @DisplayName("Should fail album creation when 'price' is negative")
    void failWhenPriceIsNegative(Double price) {
        // given
        var command = validAlbum().price(BigDecimal.valueOf(price)).build();

        // when
        var either = service.create(command);

        // then
        VavrAssertions.assertThat(either)
                .isLeft()
                .containsOnLeft(failure(new FieldViolation(FIELD_PRICE, "price must be greater than 0.", BigDecimal.valueOf(price))));
    }

    private Failure failure(FieldViolation fieldViolation) {
        return validatonFailure(VALIDATION_FAILURE_MESSAGE, List.of(fieldViolation));
    }

    private CreateAlbumCommand.CreateAlbumCommandBuilder validAlbum() {
        return CreateAlbumCommand.builder()
                .title("Blue Train")
                .artist("John Coltrane")
                .price(BigDecimal.valueOf(56.99));
    }
}
