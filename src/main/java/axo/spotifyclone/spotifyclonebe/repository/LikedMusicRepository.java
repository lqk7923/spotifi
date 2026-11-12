package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.persistent.LikedMusic;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LikedMusicRepository extends JpaRepository<LikedMusic, UUID> {
    String USER_TRACKS_FROM = "from LikedMusic lm join Track t on lm.trackId = t.trackId " +
            "join Album a on t.albumId = a.albumId where lm.userId = :userId";

    @Query(value = "select new axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection(" +
            "t.trackId, t.trackTitle, t.trackDuration, t.author, " +
            "a.albumTitle, t.albumId, a.albumCoverKey, a.albumReleaseDate, t.trackKey) " +
            USER_TRACKS_FROM + " order by lm.id desc",
            countQuery = "select count(lm) " + USER_TRACKS_FROM)
    Page<TrackDetailsProjection> findTrackDetailsByUserId(@Param("userId") String userId, Pageable pageable);

    boolean existsByUserIdAndTrackId(String userId, UUID trackId);

    @Modifying
    @Query("delete from LikedMusic lm where lm.userId = :userId and lm.trackId = :trackId")
    void deleteLike(@Param("userId") String userId, @Param("trackId") UUID trackId);
}
