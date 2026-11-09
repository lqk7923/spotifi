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
    String SEARCH_FROM = "from Track t join Album a on t.albumId = a.albumId " +
            "where :pattern = '' or lower(t.trackTitle) like lower(:pattern) escape '!' " +
            "or lower(t.author) like lower(:pattern) escape '!'";

    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackPlaybackSourceProjection(t.trackKey) " +
            "from Track t " +
            "where t.trackId = :trackId")
    Optional<TrackPlaybackSourceProjection> findPlaybackSourceByTrackId(@Param("trackId") UUID trackId);

    @Query("Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection( " +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, t.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            "from Track t join Album a on t.albumId = a.albumId " +
            "where t.trackId = :trackId")
    Optional<TrackDetailsProjection> findDetailsByTrackId(@Param("trackId") UUID trackId);

    @Query(value = "Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection( " +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, t.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            SEARCH_FROM + " order by t.trackId",
            countQuery = "Select count(t) " + SEARCH_FROM)
    Page<TrackDetailsProjection> searchDetails(@Param("pattern") String pattern, Pageable pageable);
}
