package com.bank.customer.exception;

public class CustomerAlreadyExistsException extends Exception {
    
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
    
    public CustomerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
