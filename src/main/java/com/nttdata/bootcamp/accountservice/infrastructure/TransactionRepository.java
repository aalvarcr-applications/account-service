package com.nttdata.bootcamp.accountservice.infrastructure;

import com.nttdata.bootcamp.accountservice.model.Transaction;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeTransaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 *
 * @since 2022
 */
public interface TransactionRepository
  extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findByAccountId(String id);
    Flux<Transaction> findByAccountIdAndTypeAccount(String id, TypeAccount typeAccount);
    Mono<Long> countByDateBetweenAndAccountIdAndTypeNot(Date start,
                                                        Date end,
                                                        String accountId,
                                                        TypeTransaction type);
}
