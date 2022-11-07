package com.nttdata.bootcamp.accountservice.infrastructure;

import com.nttdata.bootcamp.accountservice.model.SavingsAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 * @since 2022
 */
public interface SavingsAccountRepository
  extends ReactiveMongoRepository<SavingsAccount, String> {
    Mono<Long> countByHolderId(String id);
    Mono<SavingsAccount> findByHolderIdAndTypeAccount(String id, TypeAccount typeAccount);
    Mono<SavingsAccount> findByNumberAndTypeAccount(String number, TypeAccount typeAccount);
    Mono<Long> countByNumber(String number);
}
