package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;

import java.util.UUID;

public interface AlbumService {
    AlbumDetailsResponse getAlbumData(UUID albumId);

    PageResponse<TrackDetailsResponse> listAlbumTracks(UUID albumId, int page, int size);
    PageResponse<AlbumDetailsResponse> searchAlbums(String query, int page, int size);
}
