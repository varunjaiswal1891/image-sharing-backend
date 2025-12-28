package com.image.varun.ImageUpload.controller;

import com.image.varun.ImageUpload.dto.UploadUrlResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

import com.image.varun.ImageUpload.model.Image;
import com.image.varun.ImageUpload.repository.ImageRepository;
import com.image.varun.ImageUpload.service.S3Service;
import java.util.Map;

@RestController
@RequestMapping("/images")
public class ImageController {
    
    private static final String BUCKET_NAME = "image-share-images-bucket";

    private final S3Service s3Service;
    private final ImageRepository imageRepository;

    public ImageController(S3Service s3Service,
                           ImageRepository imageRepository) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
    }

    // 🔒 Step 1: Get presigned URL
    @PostMapping("/presign")
    public Map<String, String> presign(
            @RequestParam String filename,
            @RequestParam(defaultValue = "image/jpg") String contentType,
            Principal principal) {

        String key =
                principal.getName() + "/" +
                System.currentTimeMillis() + "_" +
                filename;

        String uploadUrl =
                s3Service.generatePresignedUploadUrl(
                        BUCKET_NAME,
                        key,
                        contentType
                );

        String imageUrl =
                "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + key;

        return Map.of(
                "uploadUrl", uploadUrl,
                "imageUrl", imageUrl
        );
    }

    // 🔒 Step 2: Save metadata
    @PostMapping("/save")
    public Image save(@RequestBody Image image, Principal principal) {
        image.setUploadedBy(principal.getName());
        return imageRepository.save(image);
    }

    // 🔒 View gallery
    @GetMapping("/all")
    public List<Image> all() {
        return imageRepository.findAll();
    }

    // 🔒 Search
    @GetMapping("/search")
    public List<Image> search(@RequestParam String keyword) {
        return imageRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
