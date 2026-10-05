package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackFullDataProjection;
import axo.spotifyclone.spotifyclonebe.persistent.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {

    /*
    * TrackFullDataProjection(UUID trackId,
    *                           String trackTitle,
    *                           Long trackDuration,
    *                           String author,
    *                           String albumTitle,
    *                           UUID albumId,
    *                           String albumCoverKey,
    *                           Time albumReleaseDate );
    * */
    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackFullDataProjection( " +
            "t.trackId, t.trackTitle, " +
            "t.trackDuration, a.albumAuthor, " +
            "a.albumTitle, t.albumId, " +
            "a.albumCoverKey, a.albumReleaseDate) " +
            "from Track t join Album a on t.albumId = a.albumId and t.albumId = ?1")
    List<TrackFullDataProjection> findByAlbumId(UUID albumId);
}
