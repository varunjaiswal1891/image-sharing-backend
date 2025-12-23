package com.image.varun.ImageUpload.controller;

import com.image.varun.ImageUpload.dto.UploadUrlResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.image.varun.ImageUpload.model.Image;
import com.image.varun.ImageUpload.repository.ImageRepository;
import com.image.varun.ImageUpload.service.ImageService;

@RestController
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public ImageController(ImageService imageService, ImageRepository imageRepository) {
        this.imageService = imageService;
        this.imageRepository = imageRepository;
    }

    @PostMapping("/upload-url")
    public UploadUrlResponse getUploadUrl() {
        return imageService.generateUploadUrl();
    }

    @GetMapping
    public List<Image> listImages() {
        return imageRepository.findAll();
    }
}
