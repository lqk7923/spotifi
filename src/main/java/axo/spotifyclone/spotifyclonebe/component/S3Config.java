package axo.spotifyclone.spotifyclonebe.component;

/**
 * Configuration class for R2 credentials and endpoint
 * - accountId: Your Cloudflare account ID
 * - accessKey: Your R2 Access Key ID (see: <a href="https://developers.cloudflare.com/r2/api/tokens">...</a>)
 * - secretKey: Your R2 Secret Access Key (see: <a href="https://developers.cloudflare.com/r2/api/tokens">...</a>)
 */
public record S3Config(String endpoint, String accessKey, String secretKey) {
    public S3Config(String endpoint, String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = String.format("https://%s.r2.cloudflarestorage.com", endpoint);
    }
}
