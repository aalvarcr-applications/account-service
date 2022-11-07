package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.exception.AccountException;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperAccount;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperGeneric;
import com.nttdata.bootcamp.accountservice.application.service.AccountService;
import com.nttdata.bootcamp.accountservice.application.service.CurrentAccountService;
import com.nttdata.bootcamp.accountservice.application.service.FixedTermAccountService;
import com.nttdata.bootcamp.accountservice.application.service.SavingsAccountService;
import com.nttdata.bootcamp.accountservice.infrastructure.AccountRepository;
import com.nttdata.bootcamp.accountservice.model.dto.AccountDto;
import com.nttdata.bootcamp.accountservice.model.dto.CurrentAccountDto;
import com.nttdata.bootcamp.accountservice.model.dto.FixedTermAccountDto;
import com.nttdata.bootcamp.accountservice.model.dto.SavingsAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 *
 * @since 2022
 */
@Service
public class AccountServiceImpl implements AccountService<AccountDto> {
    @Autowired
    SavingsAccountService<SavingsAccountDto> savingsAccountService;
    @Autowired
    CurrentAccountService<CurrentAccountDto> currentAccountService;
    @Autowired
    FixedTermAccountService<FixedTermAccountDto> fixedTermAccountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MapperAccount mapperAccount;

    @Override
    public Mono<AccountDto> create(AccountDto accountDto) {
        switch (accountDto.getTypeAccount()){
            case SAVINGS_ACCOUNT:
                MapperGeneric<SavingsAccountDto> ms =
                  new MapperGeneric<>(SavingsAccountDto.class);
                return savingsAccountService.create(ms.toDto(accountDto))
                        .map(ms::toAccountDto);
            case FIXED_TERM_ACCOUNT:
                MapperGeneric<FixedTermAccountDto> mf =
                  new MapperGeneric<>(FixedTermAccountDto.class);
                return fixedTermAccountService.create(mf.toDto(accountDto))
                        .map(mf::toAccountDto);
            case CURRENT_ACCOUNT:
                MapperGeneric<CurrentAccountDto> mc =
                  new MapperGeneric<>(CurrentAccountDto.class);
                return currentAccountService.create(mc.toDto(accountDto))
                        .map(mc::toAccountDto);
        }
        return Mono.error(new AccountException("The bank does not support this type of account"));
    }

    @Override
    public Flux<AccountDto> findByHolderId(String holderId) {
        return accountRepository.findByHolderId(holderId)
                .map(mapperAccount::toDto);
    }
    @Override
    public Mono<AccountDto> findByNumber(String number) {
        return accountRepository.findByNumber(number)
                .map(mapperAccount::toDto);
    }
    @Override
    public Mono<Void> delete(String accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountException("Account not found")))
                .flatMap(account -> {
                    switch (account.getTypeAccount()){
                        case SAVINGS_ACCOUNT:
                            return savingsAccountService.delete(accountId);
                        case FIXED_TERM_ACCOUNT:
                            return fixedTermAccountService.delete(accountId);
                        case CURRENT_ACCOUNT:
                            return currentAccountService.delete(accountId);

                    }
                    return Mono.error(new AccountException("Error delete account"));
                });
    }

  @Override
  public Flux<AccountDto> findByCreatedDateBetweenAndProductId(Date startDate,
                                                              Date endDate,
                                                              String productId) {

    return accountRepository.findByCreatedAtBetweenAndProductId(startDate,
      endDate, productId).map(mapperAccount::toDto);

  }
}
