package com.image.varun.ImageUpload.dto;

public class UploadUrlResponse {
    private String uploadUrl;
    private String s3Key;

    public UploadUrlResponse(String uploadUrl, String s3Key) {
        this.uploadUrl = uploadUrl;
        this.s3Key = s3Key;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getS3Key() {
        return s3Key;
    }
}
