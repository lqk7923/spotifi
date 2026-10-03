package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackFullInfoProjection;
import axo.spotifyclone.spotifyclonebe.persistent.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {

    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackFullInfoProjection( " +
            "t.storageName, t.trackId, t.trackTitle, " +
            "t.trackDuration, a.albumAuthor, a.albumTitle, t.albumId) " +
            "from Track t join Album a on t.albumId = a.albumId and t.albumId = ?1")
    List<TrackFullInfoProjection> findByAlbumId(UUID albumId);
}
