package com.skkudteam3.skkusirenorder.src.controller;

import com.skkudteam3.skkusirenorder.common.response.BaseResponse;
import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;
import com.skkudteam3.skkusirenorder.src.dto.*;
import com.skkudteam3.skkusirenorder.src.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    /*
    [POST] /menu/
    STAFF
    => menu 등록
     */
    @PostMapping("/")
    public BaseResponse<BaseResponseStatus> registerMenu(@RequestBody MenuPostReqDTO menuPostReqDTO) {
        Long menuId = menuService.saveMenu(menuPostReqDTO);
        return new BaseResponse<>(BaseResponseStatus.MENU_REGISTER_SUCCESS);
    }

    /*
    [PATCH] /menu/
    STAFF
    => menu 수정
     */
    @PatchMapping("/")
    public BaseResponse<BaseResponseStatus> updateMenu(@RequestBody MenuPatchReqDTO menuPatchReqDTO) {
        menuService.updateMenu(menuPatchReqDTO);
        return new BaseResponse<>(BaseResponseStatus.MENU_UPDATE_SUCCESS);
    }

    /*
    [DELETE] /menu/
    STAFF
    => menu 삭제
     */
    @DeleteMapping("/{menuId}")
    public BaseResponse<BaseResponseStatus> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return new BaseResponse<>(BaseResponseStatus.MENU_DELETE_SUCCESS);
    }

    /*
    [GET] /menu/all/cafeteriaId=?
    STAFF, CUSTOMER
    => 해당 식당 menu들 확인 (페이징추가 필요)
     */
    @GetMapping("/all/{cafeteriaId}")
    public BaseResponse<List<MenuGetResDTO>> readMenus(@PathVariable Long cafeteriaId) {

        List<MenuGetResDTO> res = menuService.findMenus(cafeteriaId);

        return new BaseResponse<>(BaseResponseStatus.MENUS_GET_SUCCESS, res);

    }

    /*
    [GET] /menu/detail
    STAFF, CUSTOMER
    => 특정 menu 상세 확인
     */
    @GetMapping("/detail/{menuId}")
    public BaseResponse<MenuDetailGetResDTO> readMenuDetail(@PathVariable Long menuId) {

        MenuDetailGetResDTO res = menuService.findMenuDetail(menuId);

        return new BaseResponse<>(BaseResponseStatus.MENU_DETAIL_GET_SUCCESS, res);

    }

    /*
    [GET] /menu/today
    CUSTOMER
    => 오늘의 메뉴 (해당 식당 ID + 메뉴 사진 + 메뉴 이름 + 메뉴 가격)
     */
    @GetMapping("/today")
    public BaseResponse<MenuGetResDTO> readMenuToday() {

        MenuGetResDTO res = menuService.findTodayMenu();

        return new BaseResponse<>(BaseResponseStatus.MENU_TODAY_GET_SUCCESS, res);

    }

    /*
     [PATCH] /menu/soldout/{menuId}
     menu 품절로 변경
     */
    @PatchMapping("/soldout/{menuId}")
    public BaseResponse<BaseResponseStatus> soldOutMenu(@PathVariable Long menuId) {
        menuService.soldOutMenu(menuId);
        return new BaseResponse<>(BaseResponseStatus.MENU_SOLDOUT_SUCCESS);
    }

    /*
     [PATCH] /menu/onsale/{menuId}
     menu 판매중으로 변경
     */
    @PatchMapping("/onsale/{menuId}")
    public BaseResponse<BaseResponseStatus> onSaleMenu(@PathVariable Long menuId) {
        menuService.onSaleMenu(menuId);
        return new BaseResponse<>(BaseResponseStatus.MENU_SOLDOUT_SUCCESS);
    }

}
