package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.TrackInfoResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPresignedLink;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface TrackService {
    /**
     * Generate a get presigned link
     */
    TrackPresignedLink generatePresignedDownloadUrl(String bucketName, UUID objectKey, Duration expiration);

    /**
    * Lists all track's information in system
    */
    List<TrackInfoResponse> listTracksInfo();

    /**
     * Lists all track's information of a special album in system
     */
    List<TrackInfoResponse> listTracksInfoByAlbumId(UUID albumId);
}
