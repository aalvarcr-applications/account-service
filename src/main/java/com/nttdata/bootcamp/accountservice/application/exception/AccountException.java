package com.nttdata.bootcamp.accountservice.application.exception;

/**
 *
 * @since 2022
 */

public class AccountException extends RuntimeException{
    public AccountException(String message){
        super(message);
    }
}
