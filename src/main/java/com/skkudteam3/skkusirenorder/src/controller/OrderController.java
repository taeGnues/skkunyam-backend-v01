package com.skkudteam3.skkusirenorder.src.controller;

import com.skkudteam3.skkusirenorder.common.response.BaseResponse;
import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;
import com.skkudteam3.skkusirenorder.src.dto.*;
import com.skkudteam3.skkusirenorder.src.entity.OrderStatus;
import com.skkudteam3.skkusirenorder.src.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="order", description = "주문 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    /* [POST] /order
     CUSTOMER
     => 유저가 결제버튼을 누를때, 장바구니에 존재하는 물건들로 Order 객체를 생성한다.(OrderStatus=PRE-WAITING)
     그리고 해당 Order 객체를 서버에 있는 Cafeteria의 Menu를 보며 검증하고, 검증까지 성공했다면 주문번호(merchant_uid)와 결제금액을 반환한다.

    */
    @PostMapping("/")
    public BaseResponse<OrderPostResDTO> postOrder(@RequestBody OrderPostReqDTO orderPostReqDTO){
        OrderPostResDTO result = orderService.placeOrder(orderPostReqDTO);
        return new BaseResponse<>(BaseResponseStatus.ORDER_POST_SUCCESS, result);
    }

    /* [GET] /order/pre-waiting
    STAFF
    => STAFF 쪽에서 결제까지 성공한 주문목록들을 확인하는 API (접수 대기중인 주문 확인) OrderStatus = "PRE-WAITING"
     */

    @GetMapping("/pre-waiting/{cafeteriaId}")
    public BaseResponse<List<OrderGetResDTO>> readOrderPreWaiting(@PathVariable Long cafeteriaId){
        List<OrderGetResDTO> result = orderService.findOrdersByStatus(cafeteriaId, OrderStatus.PRE_WAITING);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /* [PATCH] /order/pay
    CUSTOMER
    => 클라이언트 쪽에서 PG와 통신 후 결제가 성공적으로 했다면(res.success) 해당 API로 서버에게 결제 성공 사실을 알림. 해당 order의 OrderStatus를 "WAITING"으로 변경하게 됨.
    => 이때 넘겨받은 해당 orderId를 이용함.
    => 이때, 클라이언트(가게staff)쪽으로 알람을 보내줘야함!
    \
     */

    @PatchMapping("/pay/{orderId}")
    public BaseResponse<BaseResponseStatus> payOrder(@PathVariable Long orderId){
        orderService.payOrder(orderId);
        return new BaseResponse<>(BaseResponseStatus.ORDER_PAY_SUCCESS);
        // TODO : 가게 staff 쪽으로 알람 보내기.
    }

    /* [GET] /order/waiting
    STAFF
    => STAFF 쪽에서 결제까지 성공한 주문목록들을 확인하는 API (접수 대기중인 주문 확인) OrderStatus = "WAITING"
     */

    @GetMapping("/waiting/{cafeteriaId}")
    public BaseResponse<List<OrderGetResDTO>> readOrderWaiting(@PathVariable Long cafeteriaId){
        List<OrderGetResDTO> result = orderService.findOrdersByStatus(cafeteriaId, OrderStatus.WAITING);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /* [PATCH] /order/accept
    STAFF
    => STAFF 쪽에서 예상 소요 시간(estimatedTime)을 작성 후 주문받기 버튼을 누르면 "진행중인주문"으로 변경하는 API OrderStatus = "PROCEEDING"
    (이때 손님쪽에 push알람?? :예정)
    => "밀린 주문" 내역 확인 가능.
     */

    @PatchMapping("/accept")
    public BaseResponse<BaseResponseStatus> acceptOrder(@RequestBody OrderPatchReqDTO orderPatchReqDTO){
        orderService.acceptOrder(orderPatchReqDTO);
        return new BaseResponse<>(BaseResponseStatus.ORDER_ACCEPT_SUCCESS);
    }

    /* [GET] /order/proceeding
    STAFF
    => STAFF 쪽에서 승인한 주문목록들을 확인하는 API (접수 후 진행중인 주문 확인) OrderStatus = "PROCEEDING"
     */

    @GetMapping("/proceeding/{cafeteriaId}")
    public BaseResponse<List<OrderGetResDTO>> readOrderProceeding(@PathVariable Long cafeteriaId){
        List<OrderGetResDTO> result = orderService.findOrdersByStatus(cafeteriaId, OrderStatus.PROCEEDING);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /* [PATCH] /order/complete

    => STAFF 쪽에서 승인한 주문을 완료처리하는 API (진행중인 주문을 완료된 주문으로 변경) OrderStatus = "COMPLETE"
    (이때 손님쪽에 push알람?? :예정)

    ++ 일정 시간 후 자동으로 complete으로 변경될수도.
     */

    @PatchMapping("/complete/{orderId}")
    public BaseResponse<BaseResponseStatus> completeOrder(@PathVariable Long orderId){
        orderService.completeOrder(orderId);
        return new BaseResponse<>(BaseResponseStatus.ORDER_COMPLETE_SUCCESS);
    }

    /* [GET] /order/complete

    => STAFF 쪽에서 완료된 주문을 확인하는 API
     */

    @GetMapping("/complete/{cafeteriaId}")
    public BaseResponse<List<OrderGetResDTO>> readOrderComplete(@PathVariable Long cafeteriaId){
        List<OrderGetResDTO> result = orderService.findOrdersByStatus(cafeteriaId, OrderStatus.COMPLETE);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /* [PATCH] /order/deny
    STAFF
    => STAFF 쪽에서 주문을 거절한 경우. 거절사유가 필요하다.
     */
    @PatchMapping("/deny")
    public BaseResponse<BaseResponseStatus> denyOrder(@RequestBody OrderDenyReqDTO orderDenyReqDTO){
        orderService.denyOrder(orderDenyReqDTO);
        return new BaseResponse<>(BaseResponseStatus.ORDER_DENY_SUCCESS);
    }


    /*
        =========================================== 통계용 =========================================================
     */

    /* [GET] /order/count-waiting/{cafeteriaId}
        STAFF
        => 오늘 받은 주문의 개수와 총 받은 개수 확인
     */
    @GetMapping("/count-waiting/{cafeteriaId}")
    public BaseResponse<Integer> countWaiting(@PathVariable Long cafeteriaId){
        int result = orderService.countOrdersByStatus(cafeteriaId, OrderStatus.WAITING);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /* [GET] /order/today/count-proceeding/{cafeteriaId}
        STAFF
        => 오늘 밀린 주문의 개수 확인
     */
    @GetMapping("/today/count-proceeding/{cafeteriaId}")
    public BaseResponse<Integer> countProceedingToday(@PathVariable Long cafeteriaId){
        int result = orderService.countTodayOrdersByStatus(cafeteriaId, OrderStatus.PROCEEDING);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /*  [GET] /order/today/count-completed/{cafeteriaId}
        STAFF
        => 오늘 처리한 주문의 개수 확인 (=판매량)
     */
    @GetMapping("/today/count-completed/{cafeteriaId}")
    public BaseResponse<Integer> countCompletedToday(@PathVariable Long cafeteriaId){
        int result = orderService.countTodayOrdersByStatus(cafeteriaId, OrderStatus.COMPLETE);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /*  [GET] /order/today/revenue/{cafeteriaId}
        STAFF
        => 오늘 수익금 확인
     */
    @GetMapping("/today/revenue/{cafeteriaId}")
    public BaseResponse<Integer> readRevenueToday(@PathVariable Long cafeteriaId){
        int result = orderService.readRevenueToday(cafeteriaId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /*  [GET] /order/revenue/{cafeteriaId}
        STAFF
        => 전체 총 매출 확인
     */
    @GetMapping("/revenue/{cafeteriaId}")
    public BaseResponse<Integer> readRevenueTotal(@PathVariable Long cafeteriaId){
        int result = orderService.readRevenueTotal(cafeteriaId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }


    /*  [GET] /order/today/best-menus/{cafeteriaId}
        STAFF
        => 오늘 판매량 메뉴 1등부터 4등까지 리턴
     */
    @GetMapping("/today/best-menus/{cafeteriaId}")
    public BaseResponse<List<String>> readBestMenusToday(@PathVariable Long cafeteriaId){
        List<String> result = orderService.readBestMenusToday(cafeteriaId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }


    /*  [GET] /order/takeout-check/{cafeteriaId}
        STAFF
        => 포장 손님, 비포장 손님 수 리턴
     */
    @GetMapping("/takeout-check/{cafeteriaId}")
    public BaseResponse<TakeOutCheckGetResDTO> readTakeOut(@PathVariable Long cafeteriaId){
        TakeOutCheckGetResDTO result = orderService.readTakeOut(cafeteriaId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    // TODO: 하루 평균 판매 금액 의미 확인하기.

    /*  [GET] /order/revenue-average/{cafeteriaId}
        STAFF
        => 하루 평균 판매 금액 리턴
     */
    @GetMapping("/revenue-average/{cafeteriaId}")
    public BaseResponse<Long> readRevenueAverage(@PathVariable Long cafeteriaId){
        Long result = orderService.readRevenueAverage(cafeteriaId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }
}
