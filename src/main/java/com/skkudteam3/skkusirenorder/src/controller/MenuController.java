package com.skkudteam3.skkusirenorder.src.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.skkudteam3.skkusirenorder.common.response.BaseResponse;
import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;
import com.skkudteam3.skkusirenorder.src.dto.*;
import com.skkudteam3.skkusirenorder.src.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Tag(name="menu", description = "메뉴 관련 API")
@Slf4j
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
    @Operation(summary = "메뉴 등록", description = "식당 관계자 측에서 자신의 가게에 메뉴를 등록합니다. 메뉴는 이미지와 JSON을 동시에 보내야합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메뉴 등록에 성공했을 때"),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다.")))
    })
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<BaseResponseStatus> registerMenu(@RequestPart(value = "image") MultipartFile menuImage, @RequestPart(value = "form") MenuPostReqDTO menuPostReqDTO) throws IOException {

        Long menuId = menuService.saveMenu(menuPostReqDTO, menuImage);


        return new BaseResponse<>(BaseResponseStatus.MENU_REGISTER_SUCCESS);
    }

    /*
        [GET] /menu/image/{menuid}
        STAFF, CUTOMERS
        => 메뉴 이미지 링크 (해당하는 메뉴ID로 접근가능)
     */
    @GetMapping("/image/{menuid}")
    @Operation(summary = "메뉴 이미지 조회", description = "사용자 혹은 식당관계자 측에서 선택한 메뉴의 이미지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "해당 URL이 잘못됐을 때", content = @Content(schema = @Schema(defaultValue = "해당 URL은 잘못된 경로입니다.")))
    })
    public ResponseEntity<Resource> downloadMenuImage(@PathVariable Long menuid){

        try {
            UrlResource resource = new UrlResource("file:" + menuService.getMenuImageFullPath(menuid));
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (MalformedURLException e) {
            new BaseResponse<>(BaseResponseStatus.MENU_WRONG_IMAGE_URL);
        }
        return null;
    }

    /*
    [PATCH] /menu/
    STAFF
    => menu 수정
     */

    @PatchMapping("/")
    @Operation(summary = "메뉴 수정", description = "식당 관계자 측에서 자신의 가게에 메뉴를 수정합니다. 메뉴는 이미지와 JSON을 동시에 보내야합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 수정에 성공했을 때"),
            @ApiResponse(responseCode = "404", description = "해당 menuId를 가진 메뉴가 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 menuId를 가진 메뉴가 존재하지 않습니다."))),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다.")))
    })
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
    @Operation(summary = "메뉴 삭제", description = "식당 관계자 측에서 자신의 가게에 메뉴를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 삭제에 성공했을 때", content = @Content(schema = @Schema(defaultValue = "해당 메뉴가 삭제됐습니다."))),
            @ApiResponse(responseCode = "404", description = "해당 menuId를 가진 메뉴가 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 menuId를 가진 메뉴가 존재하지 않습니다."))),
    })
    public BaseResponse<BaseResponseStatus> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return new BaseResponse<>(BaseResponseStatus.MENU_DELETE_SUCCESS);
    }

    /*
    [GET] /menu/all/{cafeteriaId}
    STAFF, CUSTOMER
    => 해당 식당 menu들 확인 (페이징추가 필요)
     */
    @GetMapping("/all/{cafeteriaId}")
    @Operation(summary = "해당 식당 모든 메뉴 확인", description = "사용자 혹은 식당 관계자 측에서 해당 가게의 모든 메뉴들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 조회에 성공했을 때"),
            @ApiResponse(responseCode = "404", description = "해당 식당에 등록된 메뉴들이 없을 때", content = @Content(schema = @Schema(defaultValue = "해당 식당에 등록된 메뉴가 없습니다."))),
    })
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
    @Operation(summary = "해당 식당 메뉴 상세 확인", description = "사용자 혹은 식당 관계자 측에서 해당 가게의 한 메뉴의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 식당 메뉴의 상세 정보를 조회했습니다."),
            @ApiResponse(responseCode = "404", description = "해당 menuId를 가진 메뉴가 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 menuId를 가진 메뉴가 존재하지 않습니다."))),
    })
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
    @Operation(summary = "오늘의 메뉴 확인", description = "오늘 열린 가게들 중에서 모든 메뉴 중에 한 개를 무작위로 뽑아 리턴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "오늘의 추천 메뉴를 조회했습니다."),
            @ApiResponse(responseCode = "404", description = "현재 열려있는 식당이 없거나 등록된 메뉴가 없을 때", content = @Content(schema = @Schema(defaultValue = "현재 열려있는 식당이 없거나 열려있는 식당들 중 등록된 메뉴가 없습니다."))),
    })
    public BaseResponse<MenuGetResDTO> readMenuToday() {

        MenuGetResDTO res = menuService.findTodayMenu();

        return new BaseResponse<>(BaseResponseStatus.MENU_TODAY_GET_SUCCESS, res);

    }

    /*
     [PATCH] /menu/soldout/{menuId}
     menu 품절로 변경
     */
    @PatchMapping("/soldout/{menuId}")
    @Operation(summary = "메뉴 품절 상태로 변경", description = "해당 메뉴를 품절 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 메뉴를 품절 상태로 변경 성공 시", content = @Content(schema = @Schema(defaultValue = "해당 메뉴를 품절 상태로 변경했습니다."))),
            @ApiResponse(responseCode = "404", description = "해당 menuId를 가진 메뉴가 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 menuId를 가진 메뉴가 존재하지 않습니다."))),

    })
    public BaseResponse<BaseResponseStatus> soldOutMenu(@PathVariable Long menuId) {
        menuService.soldOutMenu(menuId);
        return new BaseResponse<>(BaseResponseStatus.MENU_SOLDOUT_SUCCESS);
    }

    /*
     [PATCH] /menu/onsale/{menuId}
     menu 판매중으로 변경
     */
    @PatchMapping("/onsale/{menuId}")
    @Operation(summary = "메뉴 판매중 상태로 변경", description = "해당 메뉴를 판매중 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 메뉴를 판매중 상태로 변경 성공 시", content = @Content(schema = @Schema(defaultValue = "해당 메뉴를 판매중 상태로 변경했습니다."))),
            @ApiResponse(responseCode = "404", description = "해당 menuId를 가진 메뉴가 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 menuId를 가진 메뉴가 존재하지 않습니다."))),

    })
    public BaseResponse<BaseResponseStatus> onSaleMenu(@PathVariable Long menuId) {
        menuService.onSaleMenu(menuId);
        return new BaseResponse<>(BaseResponseStatus.MENU_ONSALE_SUCCESS);
    }

}
