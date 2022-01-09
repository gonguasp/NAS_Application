package com.nas.communicationsecurity.service.exception;

public class ExpiredTokenException extends Exception {

    public ExpiredTokenException(String message) {
        super(message);
    }
}
