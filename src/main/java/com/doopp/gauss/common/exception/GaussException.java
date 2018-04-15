package com.doopp.gauss.common.exception;

public class GaussException extends Exception {

    private short code = 0;

    private String message = "";

    public GaussException(short code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
