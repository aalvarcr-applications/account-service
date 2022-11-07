package com.nttdata.bootcamp.accountservice.infrastructure;

import com.nttdata.bootcamp.accountservice.model.FixedTermAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 */
public interface FixedTermAccountRepository
  extends ReactiveMongoRepository<FixedTermAccount, String> {
    Mono<Long> countByHolderIdAndTypeAccount(String id, TypeAccount typeAccount);
    Mono<FixedTermAccount> findByNumberAndTypeAccount(String number, TypeAccount typeAccount);
    Mono<Long> countByNumber(String number);
}
