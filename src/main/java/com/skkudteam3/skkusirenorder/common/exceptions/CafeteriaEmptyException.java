package com.skkudteam3.skkusirenorder.common.exceptions;


import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;

public class CafeteriaEmptyException extends BaseException{
    public CafeteriaEmptyException() {
        super(BaseResponseStatus.CAFETERIA_REGISTERED_EMPTY);
    }
}
