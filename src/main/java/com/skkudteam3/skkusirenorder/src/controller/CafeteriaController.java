package com.skkudteam3.skkusirenorder.src.controller;

import com.skkudteam3.skkusirenorder.common.response.BaseResponse;
import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;
import com.skkudteam3.skkusirenorder.src.dto.*;
import com.skkudteam3.skkusirenorder.src.entity.Cafeteria;
import com.skkudteam3.skkusirenorder.src.service.CafeteriaService;
import com.skkudteam3.skkusirenorder.src.service.CafeteriaImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Tag(name="cafeteria", description = "식당 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cafeteria")
public class CafeteriaController {
    private final CafeteriaService cafeteriaService;
    private final CafeteriaImageService cafeteriaImageService;

    /*
        [POST] /cafeteria/
        STAFF
        => 가게 이미지 + 가게 등록
     */
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "가게 이미지 및 가게 정보 등록", description = "가게 이미지 및 가게를 서비스에 등록한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "식당 이미지 저장에 성공했습니다."),
            @ApiResponse(responseCode = "406", description = "식당 이미지 저장에 실패했습니다."),
    })
    public BaseResponse<BaseResponseStatus> registerCafeteria(@RequestPart(value = "images") List<MultipartFile> imgFiles, @RequestPart(value = "form") CafeteriaPostReqDTO cafeteriaPostReqDTO) {
        Long cafeteriaId = cafeteriaService.saveCafeteria(cafeteriaPostReqDTO);

        try {
            cafeteriaImageService.storeCafeteriaImages(imgFiles, cafeteriaId);
        } catch (IOException e) {
            new BaseResponse<>(BaseResponseStatus.CAFETERIA_IMAGES_SAVE_FAILED);
        }

        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_CREATE);
    }
    /*
        [GET] /cafeteria/images
     */
    @GetMapping("/images/{cafeteriaId}")
    @Operation(summary = "가게 이미지 조회", description = "가게 이미지의 파일명들을 확인한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공했습니다."),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다."),
    })
    public BaseResponse<List<String>> readCafeteriaImages(@PathVariable Long cafeteriaId){

        List<String> res = cafeteriaImageService.findImagesByCafeteriaId(cafeteriaId);

        return new BaseResponse<>(BaseResponseStatus.SUCCESS, res);
    }

    /*
        [GET] /cafeteria/image/detail/{filename}
        STAFF, CUTOMERS
        => 가게 이미지 다운용 링크
     */
    @GetMapping("/image/detail/{filename}")
    @Operation(summary = "가게 이미지 확인 및 다운로드", description = "가게 이미지 파일명을 통해 이미지를 다운한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "해당 URL은 잘못된 경로입니다."),
    })
    public ResponseEntity<Resource> downloadCafeteriaImage(@PathVariable String filename){
        // file:경로 + 파일명
        // file: 사용시 내부 파일명 접근.
        // 보안에 취약함.
        // 이미지 타입에 따라 나눠짐.
        try {
            UrlResource resource = new UrlResource("file:" + cafeteriaImageService.getFullPath(filename));
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (MalformedURLException e) {
            new BaseResponse<>(BaseResponseStatus.CAFETERIA_WRONG_IMAGE_URL);
        }
        return null;
    }

    /*
        [PATCH] /cafeteria/open/{cafeteriaId}
        STAFF
        => 가게 개장
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/open/{cafeteria_id}")
    @Operation(summary = "가게 개장", description = "가게 영업을 시작합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "가게가 열렸습니다."),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다.")
    })
    public BaseResponse<BaseResponseStatus> openCafeteria(@PathVariable Long cafeteria_id) {
        Boolean openStatus = cafeteriaService.openCafeteria(cafeteria_id);
        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_OPEN_SUCCESS);
    }

    /*
        [PATCH] /cafeteria/close/{cafeteriaId}
        STAFF
        => 가게 폐장
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/close/{cafeteria_id}")
    @Operation(summary = "가게 폐장", description = "가게 영업을 종료합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "가게가 닫혔습니다."),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다.")
    })
    public BaseResponse<BaseResponseStatus> closeCafeteria(@PathVariable Long cafeteria_id) {
        Boolean openStatus = cafeteriaService.closeCafeteria(cafeteria_id);
        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_CLOSE_SUCCESS);
    }

    /*
        [PATCH] /cafeteria/
        STAFF
        => 가게 정보 수정
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/")
    @Operation(summary = "가게 정보 수정", description = "가게 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "가게 정보가 수정됐습니다."),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다.")
    })
    public BaseResponse<BaseResponseStatus> updateCafeteria(@RequestBody CafeteriaPatchReqDTO cafeteriaPatchReqDTO) {
        cafeteriaService.updateCafeteria(cafeteriaPatchReqDTO);
        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_UPDATE_SUCCESS);
    }

    /*
        [GET] /cafeteria/{cafeteriaId}
        STAFF
        => 가게 정보 확인 (가게 사진 + 가게 이름 + 가게 위치 + 가게 소개 + 연락처 + 영업요일 + 영업시간)
     */
    @GetMapping("/{cafeteriaId}")
    @Operation(summary = "가게 정보 확인", description = "식당 관계자 측에서 가게 정보를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 정보를 조회에 성공했을 때"),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다.")))
    })
    public BaseResponse<CafeteriaGetResDTO> readCafeteria(@PathVariable Long cafeteriaId) {
        Cafeteria cafeteria = cafeteriaService.findCafeteria(cafeteriaId);
        CafeteriaGetResDTO res = cafeteria.toCafeteriaGetResDTO();

        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_FOUND_SUCCESS, res);

    }


    /*
        [GET] /cafeteria/all/campus=?&pageNo=?&pageSize=?
        CUSTOMER
        => 모든 가게 정보 조회
     */
    @GetMapping("/all")
    @Operation(summary = "등록된 모든 가게 정보 조회", description = "사용자 측에서 모든 가게 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 정보를 조회에 성공했을 때"),
            @ApiResponse(responseCode = "404", description = "등록된 식당들이 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "등록된 식당이 존재하지 않습니다.")))
    })
    public BaseResponse<List<CafeteriaGetResDTO>> readCafeterias(
            @Schema(description = "율전캠퍼스와 명륜캠퍼스를 의미합니다. NATURAL 혹은 HUMAN의 값을 가집니다. 기본값은 NATURAL입니다.", defaultValue = "NATURAL", allowableValues = {"NATURAL", "HUMAN"})
            @RequestParam(value = "campus", defaultValue = "NATURAL", required = true)
            String campus,
            @Schema(description = "페이징 처리를 위한 값입니다. page의 시작지점을 의미하며, 기본값은 0입니다.")
            @RequestParam(value = "pageNo", defaultValue = "0", required = false)
            int pageNo,
            @Schema(description = "페이징 처리를 위한 값입니다. 가져올 page의 크기를 의미하며, 기본값은 3입니다.")
            @RequestParam(value = "pageSize", defaultValue = "3", required = false)
            int pageSize

    ) {

        List<CafeteriaGetResDTO> res = cafeteriaService.findCafeterias(pageNo, pageSize);

        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_FOUND_SUCCESS, res);

    }

    /*
        [GET] /cafeteria/detail/{cafeteriaId}
        CUSTOMER
        => 선택한 가게 상세 정보 조회 (가게사진 + 가게 이름 + 가게 위치 + 가게 소개 + 지도)
     */
    @GetMapping("/detail/{cafeteriaId}")
    @Operation(summary = "선택한 가게 상세 정보 조회", description = "사용자 측에서 선택한 가게의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 정보를 조회에 성공했을 때"),
            @ApiResponse(responseCode = "404", description = "해당 cafeteriaId를 가진 식당이 존재하지 않을 때", content = @Content(schema = @Schema(defaultValue = "해당 cafeteriaId를 가진 식당이 존재하지 않습니다.")))
    })
    public BaseResponse<CafeteriaDetailGetResDTO> readCafeteriaDetail(
            @PathVariable Long cafeteriaId
    ) {
        CafeteriaDetailGetResDTO res = cafeteriaService.findDetailCafeteria(cafeteriaId).toCafeteriaDetailGetResDTO();

        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_FOUND_SUCCESS, res);

    }

}
