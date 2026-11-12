package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;

import java.util.UUID;

public interface LikedMusicService {
    PageResponse<TrackDetailsResponse> listLikedTracks(String userId, int page, int size);

    void setTrackLiked(String userId, UUID trackId, boolean liked);
}
