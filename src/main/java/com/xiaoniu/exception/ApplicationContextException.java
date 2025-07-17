package com.xiaoniu.exception;

public class ApplicationContextException extends BeansException{
    public ApplicationContextException(String msg) {
        super(msg);
    }

    public ApplicationContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
