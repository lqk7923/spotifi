package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.TrackDataResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPresignedLink;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface TrackService {
    /**
     * Generate a get presigned link
     */
    TrackPresignedLink generatePresignedDownloadUrl(UUID objectKey, Duration expiration);

    /**
    * Lists all track's information in system
    */
    List<TrackDataResponse> listTracksInfo();

}
