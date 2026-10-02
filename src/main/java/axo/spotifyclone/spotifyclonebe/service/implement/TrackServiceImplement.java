package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.component.CloudflareR2Client;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackInfoResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPresignedLink;
import axo.spotifyclone.spotifyclonebe.repository.TrackRepository;
import axo.spotifyclone.spotifyclonebe.service.TrackService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TrackServiceImplement implements TrackService {
    private final CloudflareR2Client cloudflareR2Client;
    private final TrackRepository trackRepository;

    public TrackPresignedLink generatePresignedDownloadUrl(String bucketName, UUID objectId, Duration expiration) {
        String objectKey = trackRepository
                .findTrackKeyByIdAndStorageName(objectId, bucketName)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(builder -> builder
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())
                .build();

        PresignedGetObjectRequest presignedRequest = cloudflareR2Client.getPresigner().presignGetObject(presignRequest);
        return TrackPresignedLink.builder().trackPresignedLink(presignedRequest.url().toString()).build();
    }

    /**
     * Lists all track's information in system
     */
    @Override
    public List<TrackInfoResponse> listTrackData() {
        return trackRepository.findAll()
                .stream()
                .map(track -> TrackInfoResponse.builder()
                        .bucketName(track.getStorageName())
                        .trackId(String.valueOf(track.getTrackId()))
                        .trackDuration(track.getTrackDuration())
                        .trackTitle(track.getTrackTitle())
                        .author(track.getAuthor())
                        .build())
                .toList();
    }
}
