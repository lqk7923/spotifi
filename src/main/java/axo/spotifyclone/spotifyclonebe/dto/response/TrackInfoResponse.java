package axo.spotifyclone.spotifyclonebe.dto.response;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TrackInfoResponse {
    private String bucketName;
    private String trackId;
    private String trackTitle;
    private Long trackDuration;
    private String author;
}
