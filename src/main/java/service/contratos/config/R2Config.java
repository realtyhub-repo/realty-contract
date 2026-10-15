package service.contratos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class R2Config {

    private static final Region REGION = Region.of("auto");

    @Bean
    public S3Client s3Client(R2Properties r2) {
        return S3Client.builder()
                .endpointOverride(URI.create(r2.endpoint()))
                .region(REGION)
                .credentialsProvider(credenciales(r2))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(R2Properties r2) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(r2.endpoint()))
                .region(REGION)
                .credentialsProvider(credenciales(r2))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    private StaticCredentialsProvider credenciales(R2Properties r2) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(r2.accessKey(), r2.secretKey()));
    }
}
