package com.image.varun.ImageUpload.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {
     @Id @GeneratedValue
    private Long id;

    private String title;
    private String s3Key;
    private String uploadedBy;
}
