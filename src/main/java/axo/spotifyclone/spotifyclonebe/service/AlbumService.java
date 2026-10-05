package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDataResponse;

import java.util.UUID;

public interface AlbumService {
    /**
     * Lists all a special album's information
     */
    AlbumDataResponse getAlbumData(UUID albumId);
}
