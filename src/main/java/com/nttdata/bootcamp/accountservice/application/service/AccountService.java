package com.nttdata.bootcamp.accountservice.application.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 *
 * @since 2022
 * @param <T> Account
 */
public interface AccountService<T> extends GenericAccountService<T>{
    public Mono<T> findByNumber(String number);

    public Flux<T> findByHolderId(String holderId);

    public Flux<T> findByCreatedDateBetweenAndProductId(Date startDate,
                                                       Date endDate,
                                                       String productId);
}
