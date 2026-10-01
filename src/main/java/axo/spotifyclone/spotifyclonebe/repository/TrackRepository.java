package axo.spotifyclone.spotifyclonebe.repository;

import axo.spotifyclone.spotifyclonebe.persistent.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {
    @Query("Select t.trackKey from Track t where t.trackId = ?1 and t.storageName = ?2")
    Optional<String> findTrackKeyByIdAndStorageName(UUID trackId, String storageName);
}
