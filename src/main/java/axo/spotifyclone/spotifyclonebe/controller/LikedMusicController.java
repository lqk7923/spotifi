package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.service.LikedMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
