package com.skkudteam3.skkusirenorder.common.exceptions;


import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;

public class OrderEmptyByStatusException extends BaseException{
    public OrderEmptyByStatusException() {
        super(BaseResponseStatus.ORDER_REGISTERED_EMPTY_BY_STATUS);
    }
}
