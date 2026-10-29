package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.component.CloudflareR2Client;
import axo.spotifyclone.spotifyclonebe.dto.storage.PresignedUrl;
import axo.spotifyclone.spotifyclonebe.service.MediaUrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MediaUrlServiceImplement implements MediaUrlService {
    private final CloudflareR2Client cloudflareR2Client;
    private final Duration expiration;

    public MediaUrlServiceImplement(CloudflareR2Client cloudflareR2Client,
                                   @Value("${app.media.url-expiration:PT2M}") Duration expiration){
        if(expiration.compareTo(Duration.ofSeconds(1)) < 0 || expiration.compareTo(Duration.ofMinutes(10)) > 0){
            throw new IllegalArgumentException("Media URL expiration must be valid.");
        }
        this.cloudflareR2Client = cloudflareR2Client;
        this.expiration = expiration;
    }

    @Override
    public PresignedUrl generateUrl(String objectKey){
        if(objectKey == null || objectKey.isBlank()){
            return null;
        }
        return cloudflareR2Client.presignedGetObjectRequest(expiration, objectKey);
    }

}
