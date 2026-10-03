package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.projection.AlbumInfoResponse;

import java.util.UUID;

public interface AlbumService {
    /**
     * Lists all a special album's information
     */
    AlbumInfoResponse getAlbumData(UUID albumId);
}
