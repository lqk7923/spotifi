package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackInfoProjection;
import axo.spotifyclone.spotifyclonebe.persistent.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {
    @Query("Select t.trackKey from Track t where t.trackId = ?1 and t.storageName = ?2")
    Optional<String> findTrackKeyByIdAndStorageName(UUID trackId, String storageName);

    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackInfoProjection( t.storageName, t.trackId, t.trackTitle, " +
            "t.trackDuration, a.albumAuthor, a.albumTitle, t.albumId) " +
            "from Track t join Album a on t.albumId = a.albumId and t.albumId = ?1")
    List<TrackInfoProjection> findAllTracksInfoByAlbumId(UUID albumId);

    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackInfoProjection( t.storageName, t.trackId, t.trackTitle, " +
            "t.trackDuration, a.albumAuthor, a.albumTitle, t.albumId) " +
            "from Track t join Album a on t.albumId = a.albumId")
    List<TrackInfoProjection> findAllTracksInfo();
}
