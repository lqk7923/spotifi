package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;

public interface LikedMusicService {
    PageResponse<TrackDetailsResponse> listLikedTracks(String userId, int page, int size);
}
