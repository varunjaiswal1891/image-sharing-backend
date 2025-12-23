package com.image.varun.ImageUpload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.image.varun.ImageUpload.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    
}
