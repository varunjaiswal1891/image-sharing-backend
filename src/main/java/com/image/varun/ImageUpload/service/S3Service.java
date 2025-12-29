package com.image.varun.ImageUpload.service;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;

@Service
public class S3Service {
    private final S3Presigner presigner;
    private final S3Client s3Client;

    public S3Service(S3Presigner presigner, S3Client s3Client) {
        this.presigner = presigner;
        this.s3Client = s3Client;
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

    public void deleteObject(String bucketName, String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
