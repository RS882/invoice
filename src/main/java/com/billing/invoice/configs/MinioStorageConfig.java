package com.billing.invoice.configs;

import com.billing.invoice.exception_handler.exceptions.server_exception.exceptions.MinioException;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Configuration
public class MinioStorageConfig {
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${storage.url}")
    private String minioUrl;

    public static TimeUnit timeUnitForTempLink = TimeUnit.DAYS;

    @Bean
    public MinioClient minioClient() {
        try {
            return MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }
}
