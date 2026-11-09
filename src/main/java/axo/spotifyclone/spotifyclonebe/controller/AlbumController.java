package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.ApiResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping("")
    public ResponseEntity<PageResponse<AlbumDetailsResponse>> getAlbumsList(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(albumService.searchAlbums(q, page, size));
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<ApiResponse<AlbumDetailsResponse>> getAlbumData(@PathVariable UUID albumId){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(new ApiResponse<>(albumService.getAlbumData(albumId)));
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<PageResponse<TrackDetailsResponse>> getAlbumTracks(
            @PathVariable UUID albumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(albumService.listAlbumTracks(albumId, page, size));
    }
}
