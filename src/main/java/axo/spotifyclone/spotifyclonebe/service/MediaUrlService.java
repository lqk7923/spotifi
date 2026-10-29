package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.storage.PresignedUrl;

public interface MediaUrlService {
    PresignedUrl generateUrl(String objectKey);

}
