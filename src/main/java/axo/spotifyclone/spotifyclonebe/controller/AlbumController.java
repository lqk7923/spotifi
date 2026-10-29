package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.ApiResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping("/{albumId}")
    public ResponseEntity<ApiResponse<AlbumDetailsResponse>> getAlbumData(@PathVariable UUID albumId){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(new ApiResponse<>(albumService.getAlbumData(albumId)));
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<ApiResponse<List<TrackDetailsResponse>>> getAlbumTracks(@PathVariable UUID albumId){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(new ApiResponse<>(albumService.listAlbumTracks(albumId)));
    }
}
