package com.luckvicky.blur.domain.channelboard.exception;

import com.luckvicky.blur.global.enums.code.ErrorCode;
import com.luckvicky.blur.global.execption.BaseException;

public class NotExistChannelException extends BaseException {

    public NotExistChannelException(ErrorCode errorCode){
        super(errorCode);
    }
}
