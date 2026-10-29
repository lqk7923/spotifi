package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.persistent.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
}
