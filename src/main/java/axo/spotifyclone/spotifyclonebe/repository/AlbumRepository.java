package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import axo.spotifyclone.spotifyclonebe.persistent.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection( " +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, a.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            "from Album a left join Track t on t.albumId = a.albumId " +
            "where a.albumId = :albumId order by t.trackId")
    List<TrackDetailsProjection> findTrackDetailsByAlbumId(@Param("albumId") UUID albumId);
}
