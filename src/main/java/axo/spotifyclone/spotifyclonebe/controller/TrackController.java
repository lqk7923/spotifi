package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.ApiResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPlaybackResponse;
import axo.spotifyclone.spotifyclonebe.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @GetMapping("/{id}/playback")
    public ResponseEntity<ApiResponse<TrackPlaybackResponse>> getTrackPresignedLink(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "") String include
    ){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(new ApiResponse<>(trackService.getPlayback(id, include)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrackDetailsResponse>> getTrackData(@PathVariable UUID id){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(new ApiResponse<>(trackService.getTrackData(id)));
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<TrackDetailsResponse>> getTracksList(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(trackService.searchTracks(q, page, size));
    }
}
