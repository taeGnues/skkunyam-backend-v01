package com.skkudteam3.skkusirenorder.src.dto;

import com.skkudteam3.skkusirenorder.src.entity.Menu;
import com.skkudteam3.skkusirenorder.src.entity.MenuOptionDetail;
import com.skkudteam3.skkusirenorder.src.entity.MenuOptionGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/*
    메뉴 조회 시 사용하는 DTO
 */
@Data
@NoArgsConstructor
public class MenuGetResDTO {

    private Long menuId;
    private String name;
    private int price;
    private String menuImagePath;
    private String category;
    private Boolean isSeason;
    private Boolean isBest;

    public MenuGetResDTO(Long menuId, String name, int price, String menuImagePath, String category, Boolean isSeason, Boolean isBest) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.menuImagePath = menuImagePath;
        this.category = category;
        this.isSeason = isSeason;
        this.isBest = isBest;
    }
}
