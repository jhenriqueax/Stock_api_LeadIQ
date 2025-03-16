package com.leadiq.Stock_api_LeadIQ.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
