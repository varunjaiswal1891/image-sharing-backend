package com.image.varun.ImageUpload.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.image.varun.ImageUpload.dto.UploadUrlResponse;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
public class ImageService {
    private final String bucket = "image-share-images-bucket";

    public UploadUrlResponse generateUploadUrl() {
        String key = "images/" + UUID.randomUUID();

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_SOUTH_1)
                .build();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpg")
                .build();

        PresignedPutObjectRequest presigned =
                presigner.presignPutObject(p -> p
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(request));

        return new UploadUrlResponse(presigned.url().toString(), key);
    }
}
