package com.socialengine.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GuardrailException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public GuardrailException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
