package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.exceptions.BadRequestException;
import axo.spotifyclone.spotifyclonebe.service.LikedMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users/me/liked-music")
@RequiredArgsConstructor
public class LikedMusicController {
    private final LikedMusicService likedMusicService;

    @GetMapping
    public ResponseEntity<PageResponse<TrackDetailsResponse>> getLikedTracks(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(likedMusicService.listLikedTracks(jwt.getSubject(), page, size));
    }

    @PatchMapping("/{trackId}")
    public ResponseEntity<Void> updateLikedTrack(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID trackId,
            @RequestBody LikedTrackRequest request
    ){
        if(request.liked() == null){
            throw new BadRequestException("Liked must be provided.");
        }
        likedMusicService.setTrackLiked(jwt.getSubject(), trackId, request.liked());
        return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
    }

    public record LikedTrackRequest(Boolean liked) {}
}
