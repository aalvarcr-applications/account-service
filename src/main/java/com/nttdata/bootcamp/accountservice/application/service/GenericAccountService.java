package com.nttdata.bootcamp.accountservice.application.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 * @param <T> Account
 *
 */
public interface GenericAccountService<T> {
    public Mono<T> create(T accountDto);
    public Mono<Void> delete(String accountId);
}
