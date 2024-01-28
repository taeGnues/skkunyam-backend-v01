package com.skkudteam3.skkusirenorder.src.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuImage {

    @Id
    @Column(name = "menu_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalImageName;
    private String menuImagePath;

    private LocalDateTime createdAt;

    public MenuImage(String originalImageName, String menuImagePath) {
        this.originalImageName = originalImageName;
        this.menuImagePath = menuImagePath;
        this.createdAt = LocalDateTime.now();
    }
}
