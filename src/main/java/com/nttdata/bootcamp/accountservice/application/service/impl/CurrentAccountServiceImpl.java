package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.exception.AccountException;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperCurrentAccount;
import com.nttdata.bootcamp.accountservice.application.service.CurrentAccountService;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.*;
import com.nttdata.bootcamp.accountservice.infrastructure.CurrentAccountRepository;
import com.nttdata.bootcamp.accountservice.model.constant.StateAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeCustomer;
import com.nttdata.bootcamp.accountservice.model.constant.TypeProfile;
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
public class CurrentAccountServiceImpl implements CurrentAccountService<CurrentAccountDto> {
    @Autowired
    CustomerClientService customerClient;
    @Autowired
    ProductClientService productClient;
    @Autowired
    CreditClientService creditClient;
    @Autowired
    CurrentAccountRepository currentAccountRepository;
    @Autowired
    MapperCurrentAccount mapperCurrentAccount;
    @Override
    public Mono<CurrentAccountDto> create(CurrentAccountDto accountDto) {
        accountDto.setState(StateAccount.ACTIVE);
        accountDto.setCreatedAt(new Date());
        return customerClient.getCustomer(accountDto.getHolderId())
                .switchIfEmpty(Mono.error(new AccountException("Customer not found")))
                .flatMap(customerDto -> {
                  if (customerDto.getTypeProfile() != null
                    && customerDto.getTypeProfile().equals(TypeProfile.PYME)) {
                        return creditClient
                          .getCreditCardCustomer(accountDto.getHolderId())
                                .switchIfEmpty(Mono.error(new AccountException(
                                  "The customer must have a credit card to enable this account")))

                                .count()
                                .doOnNext(count -> {
                                    if (count == 0) {
                                      throw new AccountException(
                                        "The customer must have a credit" +
                                          " card to enable this account");
                                    }
                                })
                                .flatMap(count -> this.findAndSave(accountDto, customerDto));
                    }
                    return this.findAndSave(accountDto, customerDto);
                });
    }
    public Mono<CurrentAccountDto> findAndSave(CurrentAccountDto accountDto,
                                               CustomerDto customerDto) {
        return currentAccountRepository.countByHolderId(accountDto.getHolderId())
                .doOnNext(count -> {
                  if (count >= 1 && customerDto.getTypeCustomer()
                    .equals(TypeCustomer.PERSONAL)) {
                    throw new AccountException("The customer can only have one savings account");
                  }
                })
                .flatMap(count -> this.save(accountDto))
                .switchIfEmpty(Mono.defer(() -> this.save(accountDto)));
    }
    public Mono<CurrentAccountDto> save(CurrentAccountDto accountDto) {
        return productClient.getProductAccount(accountDto.getProductId())
                .doOnNext(productDto -> {
                    if (BigDecimal.valueOf(productDto.getMinFixedAmount())
                      .compareTo(accountDto.getBalance()) > 0) {
                        throw new AccountException(
                          "Insufficient minimum amount to open an account");
                    }
                })
                .flatMap(productDto -> currentAccountRepository
                  .countByNumber(accountDto.getNumber()))
                .doOnNext(count -> {
                    if (count > 0) {
                        throw new AccountException("Account number already exists");
                    }
                })
                .flatMap(pr -> {
                    accountDto.setTypeAccount(TypeAccount.CURRENT_ACCOUNT);
                    return Mono.just(accountDto)
                            .map(mapperCurrentAccount::toCurrentAccount)
                            .flatMap(currentAccountRepository::insert)
                            .map(mapperCurrentAccount::toDto);
                });
    }

    @Override
    public Mono<Void> delete(String accountId) {
        return currentAccountRepository.findById(accountId)
                .flatMap(currentAccountRepository::delete);
    }
}
