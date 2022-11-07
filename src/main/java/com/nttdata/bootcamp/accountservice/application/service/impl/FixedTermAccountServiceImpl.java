package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.exception.AccountException;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperFixedTerm;
import com.nttdata.bootcamp.accountservice.application.service.FixedTermAccountService;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.CustomerClientService;
import com.nttdata.bootcamp.accountservice.infrastructure.FixedTermAccountRepository;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.ProductClientService;
import com.nttdata.bootcamp.accountservice.model.constant.StateAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeCustomer;
import com.nttdata.bootcamp.accountservice.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @since 2022
 */
@Service
public class FixedTermAccountServiceImpl implements FixedTermAccountService<FixedTermAccountDto> {
    @Autowired
    CustomerClientService customerClient;
    @Autowired
    ProductClientService productClient;
    @Autowired
    FixedTermAccountRepository fixedTermAccountRepository;
    @Autowired
    MapperFixedTerm mapperFixedTerm;
    @Override
    public Mono<FixedTermAccountDto> create(FixedTermAccountDto accountDto) {
        accountDto.setState(StateAccount.ACTIVE);
        accountDto.setCreatedAt(new Date());
        return customerClient.getCustomer(accountDto.getHolderId())
          .switchIfEmpty(Mono.error(new AccountException("Customer not found")))
          .doOnNext(customerDto -> {
            if (customerDto.getTypeCustomer().equals(TypeCustomer.COMPANY)) {
              throw new AccountException("Customer must be personal type");
            }
          }).flatMap(client -> fixedTermAccountRepository
            .countByHolderIdAndTypeAccount(accountDto.getHolderId(),
              TypeAccount.FIXED_TERM_ACCOUNT)
            .doOnNext(count -> {
              if (count >= 1) {
                throw new AccountException(
                  "The customer can only have one fixed term deposit account");
              }
            }).flatMap(count -> this.save(accountDto))
            .switchIfEmpty(Mono.defer(() -> this.save(accountDto))));
    }
    public Mono<FixedTermAccountDto> save(FixedTermAccountDto accountDto) {
        return productClient.getProductAccount(accountDto.getProductId())
                .switchIfEmpty(Mono.error(new AccountException("Product does not exist")))
                .doOnNext(productDto -> {
                    if (BigDecimal.valueOf(productDto.getMinFixedAmount())
                      .compareTo(accountDto.getBalance()) > 0) {
                        throw new AccountException(
                          "Insufficient minimum amount to open an account");
                    }
                })
                .flatMap(productDto -> fixedTermAccountRepository
                  .countByNumber(accountDto.getNumber()))
                .doOnNext(count -> {
                    if (count > 0) {
                        throw new AccountException(
                          "Account number already exists");
                    }
                })
                .flatMap(productAccountDto -> {
                    accountDto.setTypeAccount(TypeAccount.FIXED_TERM_ACCOUNT);
                    return Mono.just(accountDto)
                            .map(mapperFixedTerm::toProductAccount)
                            .flatMap(fixedTermAccountRepository::insert)
                            .map(mapperFixedTerm::toDto);
                });
    };
    @Override
    public Mono<Void> delete(String accountId) {
        return fixedTermAccountRepository.findById(accountId)
                .flatMap(fixedTermAccountRepository::delete);
    }
}
