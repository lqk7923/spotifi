package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackInfoProjection;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackFullDataProjection;
import axo.spotifyclone.spotifyclonebe.persistent.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {
    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackInfoProjection(t.trackKey, a.albumCoverKey) " +
            "from Track t join Album a on t.albumId = a.albumId " +
            "where t.trackId = ?1")
    Optional<TrackInfoProjection> findTrackKeyById(UUID trackId);

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
            "t.trackId, " +
            "t.trackTitle, " +
            "t.trackDuration, " +
            "a.albumAuthor, " +
            "a.albumTitle, " +
            "t.albumId, " +
            "a.albumCoverKey, " +
            "a.albumReleaseDate) " +
            "from Track t join Album a on t.albumId = a.albumId")
    List<TrackFullDataProjection> findAllTracksInfo();
}
