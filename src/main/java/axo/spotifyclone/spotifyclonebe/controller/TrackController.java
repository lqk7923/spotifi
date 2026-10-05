package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.TrackDataResponse;
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

    @GetMapping("/track/{id}")
    public TrackPresignedLink getTrackPresignedLink(
        @PathVariable UUID id
    ){
        return trackService.generatePresignedDownloadUrl(id, Duration.ofMinutes(2));
    }

    @GetMapping("/all")
    public List<TrackDataResponse> getTracksList(){
        return trackService.listTracksInfo();
    }
}
