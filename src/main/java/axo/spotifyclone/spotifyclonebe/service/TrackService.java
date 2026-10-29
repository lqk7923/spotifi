package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPlaybackResponse;

import java.util.UUID;

public interface TrackService {
    TrackPlaybackResponse getPlayback(UUID trackId, String include);

    TrackDetailsResponse getTrackData(UUID trackId);

    PageResponse<TrackDetailsResponse> listTracksInfo(int page, int size);
}
