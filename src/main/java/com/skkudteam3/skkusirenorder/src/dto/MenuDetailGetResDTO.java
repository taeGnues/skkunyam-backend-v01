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
public class MenuDetailGetResDTO {

    private Long menuId;
    private String name;
    private int price;
    private String category;
    private Boolean isSeason;
    private Boolean isBest;
    private List<MenuOptionGroupDTO> menuOptionGroupDTOS = new ArrayList<>();

    public MenuDetailGetResDTO(Long menuId, String name, int price, String category, Boolean isSeason, Boolean isBest, List<MenuOptionGroupDTO> menuOptionGroupDTOS) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.isSeason = isSeason;
        this.isBest = isBest;
        this.menuOptionGroupDTOS = menuOptionGroupDTOS;
    }

    @Data
    @NoArgsConstructor
    public static class MenuOptionGroupDTO{
        private String name;
        private Boolean isEssentialOption;
        private List<MenuOptionDetailDTO> menuOptionDetailDTOs = new ArrayList<>();

        public MenuOptionGroupDTO(String name, Boolean isEssentialOption, List<MenuOptionDetailDTO> menuOptionDetailDTOs) {
            this.name = name;
            this.isEssentialOption = isEssentialOption;
            this.menuOptionDetailDTOs = menuOptionDetailDTOs;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MenuOptionDetailDTO {
        private String name;
        private int price;

        public MenuOptionDetailDTO(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public MenuOptionDetail toEntity(){
            return new MenuOptionDetail(name, price);
        }
    }
}
