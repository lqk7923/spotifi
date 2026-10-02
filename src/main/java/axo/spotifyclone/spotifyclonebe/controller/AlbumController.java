package axo.spotifyclone.spotifyclonebe.controller;

import axo.spotifyclone.spotifyclonebe.dto.response.TrackInfoResponse;
import axo.spotifyclone.spotifyclonebe.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {
    private final TrackService trackService;

    @GetMapping("/{albumId}/tracks")
    public List<TrackInfoResponse> getTracksInfoInTheAlbum(@PathVariable UUID albumId){
        return trackService.listTracksInfoByAlbumId(albumId);
    }

}
