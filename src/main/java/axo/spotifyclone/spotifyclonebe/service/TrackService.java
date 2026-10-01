package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.TrackInfoResponse;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface TrackService {
    /**
     * Generate a get presigned link
     */
    String generatePresignedDownloadUrl(String bucketName, UUID objectKey, Duration expiration);

    /**
    * Lists all track's information in system
    */
    List<TrackInfoResponse> listTrackData();
}
