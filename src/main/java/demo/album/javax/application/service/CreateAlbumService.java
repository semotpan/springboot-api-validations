package demo.album.javax.application.service;

import demo.album.javax.application.port.in.CreateAlbumUseCase;
import demo.album.javax.domain.Album;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("jCreateAlbumUseCase")
class CreateAlbumService implements CreateAlbumUseCase {

    @Override
    public UUID add(CreateAlbumCommand command) {
        // TODO: validate business rules
        // requireUniqueTitle(command.title());

        // TODO: manipulate model state
        var album = Album.builder()
                .id(UUID.randomUUID())
                .title(command.getTitle())
                .artist(command.getArtist())
                .price(command.getPrice())
                .build();


        // TODO: return output
        return album.id();
    }
}
