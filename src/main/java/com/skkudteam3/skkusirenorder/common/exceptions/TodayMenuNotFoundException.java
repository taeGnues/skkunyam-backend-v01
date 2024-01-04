package com.skkudteam3.skkusirenorder.common.exceptions;


import com.skkudteam3.skkusirenorder.common.response.BaseResponseStatus;

public class TodayMenuNotFoundException extends BaseException{
    public TodayMenuNotFoundException() {
        super(BaseResponseStatus.TODAY_MENU_NOT_FOUND);
    }
}
