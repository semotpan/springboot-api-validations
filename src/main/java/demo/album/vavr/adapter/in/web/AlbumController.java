package demo.album.vavr.adapter.in.web;

import demo.album.vavr.application.port.in.CreateAlbumUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

import static demo.album.vavr.application.port.in.CreateAlbumUseCase.CreateAlbumCommand;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController("vAlbumController")
@RequiredArgsConstructor
@RequestMapping(value = "/vavr/albums")
final class AlbumController {

    private final CreateAlbumUseCase vCreateAlbumUseCase;
    private final ApiFailureHandler apiFailureHandler;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<?> create(@RequestBody AlbumResource resource) {
        var command = CreateAlbumCommand.builder()
                .title(resource.title())
                .artist(resource.artist())
                .price(resource.price())
                .build();

        return vCreateAlbumUseCase.create(command)
                .fold(apiFailureHandler::handle, albumId -> created(fromCurrentRequest().path("/{id}").build(albumId)).build());
    }

    private record AlbumResource(UUID id,
                                 String title,
                                 String artist,
                                 BigDecimal price) {
    }
}
