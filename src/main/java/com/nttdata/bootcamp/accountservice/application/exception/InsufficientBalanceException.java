package com.nttdata.bootcamp.accountservice.application.exception;

/**
 *
 * @since 2022
 */

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
