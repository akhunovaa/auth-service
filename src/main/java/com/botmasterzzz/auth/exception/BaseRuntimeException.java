package com.botmasterzzz.auth.exception;

import org.springframework.http.HttpStatus;

public class BaseRuntimeException extends Throwable {
    public BaseRuntimeException(String message, HttpStatus unauthorized) {
    }
}
