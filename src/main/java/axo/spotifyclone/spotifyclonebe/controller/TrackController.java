package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.TrackInfoResponse;
import axo.spotifyclone.spotifyclonebe.service.implement.TrackServiceImplement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api-test")
@AllArgsConstructor
public class TrackController {
    private final TrackServiceImplement trackService;

    @GetMapping("/{bucket}/{id}")
    public String getTrackPresignedUrl(
        @PathVariable String bucket,
        @PathVariable UUID id
    ){
        return trackService.generatePresignedDownloadUrl(bucket, id, Duration.ofMinutes(2));
    }

    @GetMapping("/all")
    public List<TrackInfoResponse> getTrackList(){
        return trackService.listTrackData();
    }
}
