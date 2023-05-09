package com.shorturl.exception;

import com.shorturl.api.IErrorCode;

public class ApiException extends RuntimeException {

    private IErrorCode errorCode;

    public ApiException(IErrorCode iErrorCode) {
        super(iErrorCode.getMessage());
        this.errorCode = iErrorCode;
    }

    public ApiException(String message, IErrorCode iErrorCode) {
        super(message);
        this.errorCode = iErrorCode;
    }

    public ApiException(String message, Throwable cause, IErrorCode iErrorCode) {
        super(message, cause);
        this.errorCode = iErrorCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public IErrorCode getErrorCode(){
        return errorCode;
    }

}
