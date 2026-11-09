package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import axo.spotifyclone.spotifyclonebe.persistent.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    String ALBUM_TRACKS_FROM = "from Track t join Album a on t.albumId = a.albumId " +
            "where a.albumId = :albumId";

    @Query("Select a from Album a " +
            "where :pattern = '' or lower(a.albumTitle) like lower(:pattern) escape '!' " +
            "or lower(a.albumAuthor) like lower(:pattern) escape '!' order by a.albumId")
    Page<Album> searchAlbums(@Param("pattern") String pattern, Pageable pageable);

    @Query(value = "Select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection( " +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, a.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            ALBUM_TRACKS_FROM + " order by t.trackId",
            countQuery = "Select count(t) " + ALBUM_TRACKS_FROM)
    Page<TrackDetailsProjection> findTrackDetailsByAlbumId(@Param("albumId") UUID albumId, Pageable pageable);
}
