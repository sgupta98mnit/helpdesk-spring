package com.sumit.helpdesk.common;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
