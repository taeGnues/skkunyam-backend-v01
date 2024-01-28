package com.skkudteam3.skkusirenorder.src.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CafeteriaImage {

    @Id
    @Column(name = "cafeteria_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalImageName;
    private String cafeteriaImagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_id")
    private Cafeteria cafeteria;

    private LocalDateTime createdAt;

    public CafeteriaImage(String originalImageName, String cafeteriaImagePath) {
        this.originalImageName = originalImageName;
        this.cafeteriaImagePath = cafeteriaImagePath;
        this.createdAt = LocalDateTime.now();
    }

    public void setCafeteria(Cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }
}
