package com.image.varun.ImageUpload.service;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;

@Service
public class S3Service {
    private final S3Presigner presigner;

    public S3Service(S3Presigner presigner) {
        this.presigner = presigner;
    }

    public String generatePresignedUploadUrl(
            String bucketName,
            String objectKey,
            String contentType
    ) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest)
                        .build();

        return presigner
                .presignPutObject(presignRequest)
                .url()
                .toString();
    }
}
