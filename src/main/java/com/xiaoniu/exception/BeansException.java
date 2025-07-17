package com.xiaoniu.exception;



public abstract class BeansException extends RuntimeException {
    public BeansException() {
    }

    public BeansException(String message) {
        super(message);
    }
    public BeansException( String msg,  Throwable cause) {
        super(msg, cause);
    }
}
