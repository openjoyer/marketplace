package com.openjoyer.marketplace.profile_service.exceptions;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
    public EmailExistsException() {}
}
