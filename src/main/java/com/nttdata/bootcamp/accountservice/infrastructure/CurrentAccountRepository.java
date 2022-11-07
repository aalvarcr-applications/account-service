package com.nttdata.bootcamp.accountservice.infrastructure;

import com.nttdata.bootcamp.accountservice.model.CurrentAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 */
public interface CurrentAccountRepository
  extends ReactiveMongoRepository<CurrentAccount, String> {
    Mono<Long> countByHolderId(String id);
    Mono<CurrentAccount> findByNumberAndTypeAccount(String number, TypeAccount typeAccount);
    Mono<Long> countByNumber(String number);
}
