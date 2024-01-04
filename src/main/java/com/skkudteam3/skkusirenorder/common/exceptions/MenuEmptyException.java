package com.skkudteam3.skkusirenorder.common.exceptions;


import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;

public class MenuEmptyException extends BaseException{
    public MenuEmptyException() {
        super(BaseResponseStatus.MENU_REGISTERED_EMPTY);
    }
}
