package com.openjoyer.marketplace.profile_service.exceptions;

public class AddressExistsException extends RuntimeException {
    public AddressExistsException(String message) {
        super(message);
    }
}
