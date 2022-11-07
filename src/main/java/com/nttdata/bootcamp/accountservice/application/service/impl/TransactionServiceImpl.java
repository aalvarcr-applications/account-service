package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.service.TransactionCurrentAccountService;
import com.nttdata.bootcamp.accountservice.application.service.TransactionFixedTermAccountService;
import com.nttdata.bootcamp.accountservice.application.service.TransactionSavingsAccountService;
import com.nttdata.bootcamp.accountservice.application.service.TransactionService;
import com.nttdata.bootcamp.accountservice.infrastructure.AccountRepository;
import com.nttdata.bootcamp.accountservice.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.transform.TransformerException;

/**
 *
 * @since 2022
 */
@Service
public class TransactionServiceImpl implements TransactionService<TransactionDto> {
    @Autowired
    TransactionSavingsAccountService<SavingsAccountDto> transactionSavingsAccountService;
    @Autowired
    TransactionCurrentAccountService<CurrentAccountDto> transactionCurrentAccountService;
    @Autowired
    TransactionFixedTermAccountService<FixedTermAccountDto> transactionFixedTermAccountService;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public Mono<TransactionDto> deposit(DepositDto depositDto) {
        return accountRepository.findByNumber(depositDto.getTargetAccount())
                .flatMap(account -> {
                    switch (account.getTypeAccount()){
                        case SAVINGS_ACCOUNT:
                            return transactionSavingsAccountService.deposit(depositDto);
                        case FIXED_TERM_ACCOUNT:
                            return transactionFixedTermAccountService.deposit(depositDto);
                        case CURRENT_ACCOUNT:
                            return transactionCurrentAccountService.deposit(depositDto);
                    }
                    return Mono.error(new TransformerException("Account not supported error"));
                }).switchIfEmpty(Mono.error(new TransformerException("Account does not exist")));
    }

    @Override
    public Mono<TransactionDto> withdraw(WithdrawDto withdrawDto) {
        return accountRepository.findByNumber(withdrawDto.getAccountNumber())
                .flatMap(account -> {
                    switch (account.getTypeAccount()){
                        case SAVINGS_ACCOUNT:
                            return transactionSavingsAccountService.withdraw(withdrawDto);
                        case FIXED_TERM_ACCOUNT:
                            return transactionFixedTermAccountService.withdraw(withdrawDto);
                        case CURRENT_ACCOUNT:
                            return transactionCurrentAccountService.withdraw(withdrawDto);
                    }
                    return Mono.error(new TransformerException("Account not supported error"));
                }).switchIfEmpty(Mono.error(new TransformerException("Account does not exist")));
    }

    @Override
    public Mono<TransactionDto> wireTransfer(TransactionRequestDto transferDto) {
        return accountRepository.findByNumber(transferDto.getSourceAccount())
                .flatMap(account -> {
                    switch (account.getTypeAccount()){
                        case SAVINGS_ACCOUNT:
                            return transactionSavingsAccountService.wireTransfer(transferDto);
                        case FIXED_TERM_ACCOUNT:
                            return transactionFixedTermAccountService.wireTransfer(transferDto);
                        case CURRENT_ACCOUNT:
                            return transactionCurrentAccountService.wireTransfer(transferDto);
                    }
                    return Mono.error(new TransformerException("Account not supported error"));
                }).switchIfEmpty(Mono.error(new TransformerException("Account does not exist")));
    }

    @Override
    public Flux<TransactionDto> listTransactions(String accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new TransformerException("Account does not exist")))
                .flatMapMany(account -> {
                    switch (account.getTypeAccount()){
                        case SAVINGS_ACCOUNT:
                            return transactionSavingsAccountService.listTransactions(accountId);
                        case FIXED_TERM_ACCOUNT:
                            return transactionFixedTermAccountService.listTransactions(accountId);
                        case CURRENT_ACCOUNT:
                            return transactionCurrentAccountService.listTransactions(accountId);
                    }
                    return Flux.error(new TransformerException("Account does not exist"));
                });
    }
}
