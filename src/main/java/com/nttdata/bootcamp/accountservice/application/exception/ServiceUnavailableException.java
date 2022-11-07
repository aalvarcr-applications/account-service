package com.nttdata.bootcamp.accountservice.application.exception;

/**
 *
 * @since 2022
 */

public class ServiceUnavailableException extends RuntimeException{
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
