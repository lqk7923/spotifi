package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.TrackInfoResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPresignedLink;
import axo.spotifyclone.spotifyclonebe.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/track")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @GetMapping("/{bucket}/{id}")
    public TrackPresignedLink getTrackPresignedLink(
        @PathVariable String bucket,
        @PathVariable UUID id
    ){
        return trackService.generatePresignedDownloadUrl(bucket, id, Duration.ofMinutes(2));
    }

    @GetMapping("/all")
    public List<TrackInfoResponse> getTracksList(){
        return trackService.listTracksInfo();
    }
}
