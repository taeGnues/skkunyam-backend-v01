package com.skkudteam3.skkusirenorder.common.exceptions;


import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;

public class MenuNotFoundException extends BaseException{
    public MenuNotFoundException() {
        super(BaseResponseStatus.MENU_NOT_FOUND);
    }
}
