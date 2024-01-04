package com.skkudteam3.skkusirenorder.common.exceptions;


import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;

public class OrderNotFoundException extends BaseException{
    public OrderNotFoundException() {
        super(BaseResponseStatus.ORDER_NOT_FOUND);
    }
}
