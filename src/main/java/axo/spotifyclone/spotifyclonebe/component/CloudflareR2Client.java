package axo.spotifyclone.spotifyclonebe.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Component
@Getter
public class CloudflareR2Client {
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public CloudflareR2Client(@Value("${r2-storage.account-id}")String accountId,
                              @Value("${r2-storage.access-key}")String accessKey,
                              @Value("${r2-storage.secret-key}")String secretKey){
        S3Config config = new S3Config(accountId, accessKey, secretKey);
        this.s3Client = buildS3Client(config);
        this.presigner = buildS3Presigner(config);
    }

    /**
     * Builds and configures the S3 presigner with R2-specific settings
     */
    private static S3Presigner buildS3Presigner(S3Config config) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                config.accessKey(),
                config.secretKey()
        );

        return S3Presigner.builder()
                .endpointOverride(URI.create(config.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto")) // Required by SDK but not used by R2
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    /**
     * Builds and configures the S3 client with R2-specific settings
     */
    private static S3Client buildS3Client(S3Config config) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                config.accessKey(),
                config.secretKey()
        );

        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .chunkedEncodingEnabled(false)
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(config.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto")) // Required by SDK but not used by R2
                .serviceConfiguration(serviceConfiguration)
                .build();
    }
}
