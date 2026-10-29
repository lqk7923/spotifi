package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackPlaybackSourceProjection;
import axo.spotifyclone.spotifyclonebe.persistent.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {
    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackPlaybackSourceProjection(t.trackKey) " +
            "from Track t " +
            "where t.trackId = :trackId")
    Optional<TrackPlaybackSourceProjection> findPlaybackSourceByTrackId(@Param("trackId") UUID trackId);

    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection( " +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, t.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            "from Track t left join Album a on t.albumId = a.albumId " +
            "where t.trackId = :trackId")
    Optional<TrackDetailsProjection> findDetailsByTrackId(@Param("trackId") UUID trackId);

    @Query(value = "Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection( " +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, t.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            "from Track t left join Album a on t.albumId = a.albumId order by t.trackId",
            countQuery = "Select count(t) from Track t")
    Page<TrackDetailsProjection> findAllDetails(Pageable pageable);

    @Query(value = "Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection( " +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, t.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            "from Track t left join Album a on t.albumId = a.albumId " +
            "where t.albumId = :albumId order by t.trackId",
            countQuery = "Select count(t) from Track t where t.albumId = :albumId")
    Page<TrackDetailsProjection> findDetailsByAlbumId(@Param("albumId") UUID albumId, Pageable pageable);
}
