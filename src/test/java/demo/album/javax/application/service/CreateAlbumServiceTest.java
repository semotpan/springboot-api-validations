package demo.album.javax.application.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static demo.album.javax.application.port.in.CreateAlbumUseCase.CreateAlbumCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

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
        // when
        var albumId = service.add(validAlbum().build());

        // then
        assertThat(albumId).isNotNull();
        assertThat(albumId).isInstanceOf(UUID.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should fail album creation when 'title' is blank")
    void failWhenTitleIsBlank(String title) {
        // when
        var thrown = catchThrowable(() -> validAlbum().title(title).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("title: title cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should fail album creation when 'artist' is blank")
    void failWhenArtistIsBlank(String artist) {
        // when
        var thrown = catchThrowable(() -> validAlbum().artist(artist).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("artist: artist cannot be blank.");
    }

    @Test
    @DisplayName("Should fail album creation when 'price' is null")
    void failWhenPriceIsNull() {
        // when
        var thrown = catchThrowable(() -> validAlbum().price(null).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price: price cannot be null.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10, 0})
    @DisplayName("Should fail album creation when 'price' is negative")
    void failWhenPriceIsNegative(Double price) {
        // when
        var thrown = catchThrowable(() -> validAlbum().price(BigDecimal.valueOf(price)).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price: price must be greater than 0.");
    }

    private CreateAlbumCommand.CreateAlbumCommandBuilder validAlbum() {
        return CreateAlbumCommand.builder()
                .title("Blue Train")
                .artist("John Coltrane")
                .price(BigDecimal.valueOf(56.99));
    }
}
