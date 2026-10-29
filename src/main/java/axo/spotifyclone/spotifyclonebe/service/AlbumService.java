package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;

import java.util.List;
import java.util.UUID;

public interface AlbumService {
    AlbumDetailsResponse getAlbumData(UUID albumId);

    List<TrackDetailsResponse> listAlbumTracks(UUID albumId);
}
