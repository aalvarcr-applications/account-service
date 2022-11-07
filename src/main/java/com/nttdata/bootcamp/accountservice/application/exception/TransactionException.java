package com.nttdata.bootcamp.accountservice.application.exception;

/**
 *
 * @since 2022
 */

public class TransactionException extends RuntimeException{
    public TransactionException(String message){
        super(message);
    }
}
