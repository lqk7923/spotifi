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
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.time.Duration;

@Component
@Getter
public class CloudflareR2Client {
    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucketName;

    public CloudflareR2Client(@Value("${r2-storage.account-id}")String accountId,
                              @Value("${r2-storage.access-key}")String accessKey,
                              @Value("${r2-storage.secret-key}")String secretKey,
                              @Value("${r2-storage.bucket-name}")String bucketName){
        S3Config config = new S3Config(accountId, accessKey, secretKey);
        this.s3Client = buildS3Client(config);
        this.presigner = buildS3Presigner(config);
        this.bucketName = bucketName;
    }

    /**
     * Builds and configures the S3 presigner with R2-specific settings
     */
    private S3Presigner buildS3Presigner(S3Config config) {
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
    private S3Client buildS3Client(S3Config config) {
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

    /**
     * Generate a get presigned link
     */
    public String presignedGetObjectRequest(Duration expiration, String objectKey){
        GetObjectPresignRequest objectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(builder -> builder
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())
                .build();
        return this.getPresigner().presignGetObject(objectPresignRequest).url().toString();
    }
}
