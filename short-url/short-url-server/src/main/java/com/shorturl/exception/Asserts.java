package com.shorturl.exception;

import com.shorturl.api.IErrorCode;

//用于抛出各种API异常
public class Asserts {

    public static void fail(String message){
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode){
        throw new ApiException(errorCode);
    }
}
