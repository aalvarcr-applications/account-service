package com.nttdata.bootcamp.accountservice.application.service;

import com.nttdata.bootcamp.accountservice.model.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 * @param <T> Account
 *
 */
public interface TransactionService<T> {
    Mono<TransactionDto> deposit(DepositDto depositDto);
    Mono<TransactionDto> withdraw(WithdrawDto withdrawDto);
    Mono<TransactionDto> wireTransfer(TransactionRequestDto transferDto);
    Flux<TransactionDto> listTransactions(String accountId);
}
