package com.anzhiyule.feignstance.exception;

public class FeignInstanceException extends RuntimeException {

    public FeignInstanceException() {
        super();
    }

    public FeignInstanceException(String message) {
        super(message);
    }

    public FeignInstanceException(Throwable e) {
        super(e);
    }

    public FeignInstanceException(String message, Throwable e) {
        super(message, e);
    }
}
