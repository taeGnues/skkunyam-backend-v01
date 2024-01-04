package com.skkudteam3.skkusirenorder.src.controller;

import com.skkudteam3.skkusirenorder.common.Constant;
import com.skkudteam3.skkusirenorder.common.response.BaseResponse;
import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;
import com.skkudteam3.skkusirenorder.src.dto.*;
import com.skkudteam3.skkusirenorder.src.entity.Cafeteria;
import com.skkudteam3.skkusirenorder.src.service.CafeteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/cafeteria")
public class CafeteriaController {
    private final CafeteriaService cafeteriaService;

    /*
        [POST] /cafeteria/
        STAFF
        => 가게 등록
     */
    @PostMapping("/")
    public BaseResponse<BaseResponseStatus> registerCafeteria(@RequestBody CafeteriaPostReqDTO cafeteriaPostReqDTO) {
        Long cafeteriaId = cafeteriaService.saveCafeteria(cafeteriaPostReqDTO);
        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_CREATE);
    }


    /*
        [PATCH] /cafeteria/open/{cafeteriaId}
        STAFF
        => 가게 개장
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/open/{cafeteria_id}")
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
    public BaseResponse<BaseResponseStatus> closeCafeteria(@PathVariable Long cafeteria_id) {
        Boolean openStatus = cafeteriaService.closeCafeteria(cafeteria_id);
        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_CLOSE_SUCCESS);
    }

    /*
        [PATCH] /cafeteria/
        STAFF
        => 가게 정보 수정
     */
    @PatchMapping("/")
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
    public BaseResponse<List<CafeteriaGetResDTO>> readCafeterias(
            @RequestParam(value = "campus", defaultValue = "NATURAL", required = true) String campus,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) int pageSize

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
    public BaseResponse<CafeteriaDetailGetResDTO> readCafeteriaDetail(
            @PathVariable Long cafeteriaId
    ) {
        CafeteriaDetailGetResDTO res = cafeteriaService.findDetailCafeteria(cafeteriaId).toCafeteriaDetailGetResDTO();

        return new BaseResponse<>(BaseResponseStatus.CAFETERIA_FOUND_SUCCESS, res);

    }

}
