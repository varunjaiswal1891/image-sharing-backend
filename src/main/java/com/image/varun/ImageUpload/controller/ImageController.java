package com.image.varun.ImageUpload.controller;

import com.image.varun.ImageUpload.dto.UploadUrlResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/images")
public class ImageController {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private static final String BUCKET_NAME = "image-share-images-bucket";

    private final S3Service s3Service;
    private final ImageRepository imageRepository;

    public ImageController(S3Service s3Service,
                           ImageRepository imageRepository) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
    }

    // 🔒 Step 1: Get presigned URL
    @GetMapping("/presign")
    public Map<String, String> presign(
            @RequestParam String filename,
            @RequestParam(defaultValue = "image/jpeg") String contentType,
            Principal principal) {

        logger.info("Presign request received - User: {}, Filename: {}, ContentType: {}", 
                    principal.getName(), filename, contentType);

        String key =
                principal.getName() + "/" +
                System.currentTimeMillis() + "_" +
                filename;

        logger.info("Generated S3 key: {}", key);

        String uploadUrl =
                s3Service.generatePresignedUploadUrl(
                        BUCKET_NAME,
                        key,
                        contentType
                );

        String imageUrl =
                "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + key;

        logger.info("Presigned URL generated successfully for key: {}", key);

        return Map.of(
                "uploadUrl", uploadUrl,
                "imageUrl", imageUrl,
                "contentType", contentType
        );
    }

    // 🔒 Step 2: Save metadata
    @PostMapping("/save")
    public Image save(@RequestBody Image image, Principal principal) {
        logger.info("Save image metadata request - User: {}, Title: {}, ImageUrl: {}", 
                    principal.getName(), image.getTitle(), image.getImageUrl());
        
        image.setUploadedBy(principal.getName());
        Image savedImage = imageRepository.save(image);
        
        logger.info("Image metadata saved successfully - ID: {}, User: {}", 
                    savedImage.getId(), savedImage.getUploadedBy());
        
        return savedImage;
    }

    // 🔒 View gallery
    @GetMapping("/all")
    public List<Image> all() {
        logger.info("Fetching all images from gallery");
        List<Image> images = imageRepository.findAll();
        logger.info("Retrieved {} images from gallery", images.size());
        return images;
    }

    // 🔒 Search
    @GetMapping("/search")
    public List<Image> search(@RequestParam String keyword) {
        logger.info("Search request received - Keyword: {}", keyword);
        List<Image> results = imageRepository.findByTitleContainingIgnoreCase(keyword);
        logger.info("Search completed - Found {} images matching keyword: {}", results.size(), keyword);
        return results;
    }

    // 🔒 Delete image
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, Principal principal) {
        logger.info("Delete request received - User: {}, ImageID: {}", principal.getName(), id);
        
        Optional<Image> imageOptional = imageRepository.findById(id);
        
        if (imageOptional.isEmpty()) {
            logger.warn("Image not found - ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        Image image = imageOptional.get();
        
        // Check if the user is the owner of the image
        if (!image.getUploadedBy().equals(principal.getName())) {
            logger.warn("Unauthorized delete attempt - User: {}, Image owner: {}", 
                       principal.getName(), image.getUploadedBy());
            return ResponseEntity.status(403)
                    .body(Map.of("error", "You are not authorized to delete this image"));
        }
        
        try {
            // Extract S3 key from image URL
            // Expected format: https://image-share-images-bucket.s3.amazonaws.com/username/timestamp_filename
            String imageUrl = image.getImageUrl();
            String key = imageUrl.substring(imageUrl.indexOf(".com/") + 5);
            
            logger.info("Deleting from S3 - Key: {}", key);
            
            // Delete from S3
            s3Service.deleteObject(BUCKET_NAME, key);
            
            // Delete from database
            imageRepository.deleteById(id);
            
            logger.info("Image deleted successfully - ID: {}, User: {}", id, principal.getName());
            
            return ResponseEntity.ok(Map.of(
                    "message", "Image deleted successfully",
                    "id", id.toString()
            ));
        } catch (Exception e) {
            logger.error("Error deleting image - ID: {}, Error: {}", id, e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to delete image: " + e.getMessage()));
        }
    }
}
