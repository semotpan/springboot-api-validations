package demo.album.jakarta.adapter.in.web;

import demo.album.jakarta.application.port.in.CreateAlbumUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

import static demo.album.jakarta.application.port.in.CreateAlbumUseCase.CreateAlbumCommand;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController("jAlbumController")
@RequiredArgsConstructor
@RequestMapping(value = "/jakarta/albums")
final class AlbumController {

    private final CreateAlbumUseCase jCreateAlbumUseCase;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<?> create(@RequestBody AlbumResource resource) {
        var command = CreateAlbumCommand.builder()
                .title(resource.title())
                .artist(resource.artist())
                .price(resource.price())
                .build(); // validations are applied on build

        var albumId = jCreateAlbumUseCase.add(command);

        return created(fromCurrentRequest().path("/{id}").build(albumId)).build();
    }

    private record AlbumResource(UUID id,
                                 String title,
                                 String artist,
                                 BigDecimal price) {
    }
}
