package com.skkudteam3.skkusirenorder.common.exceptions;


import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;

public class OrderEmptyException extends BaseException{
    public OrderEmptyException() {
        super(BaseResponseStatus.ORDER_REGISTERED_EMPTY);
    }
}
