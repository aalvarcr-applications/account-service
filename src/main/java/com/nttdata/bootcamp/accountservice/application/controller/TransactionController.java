package com.nttdata.bootcamp.accountservice.application.controller;

import com.nttdata.bootcamp.accountservice.application.service.TransactionService;
import com.nttdata.bootcamp.accountservice.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 */

@RefreshScope
@RestController
public class TransactionController {
    @Autowired
    TransactionService<TransactionDto> transactionService;

    @PutMapping("account/transaction/deposit")
    public Mono<TransactionDto> deposit(@RequestBody DepositDto depositDto){
        return transactionService.deposit(depositDto);
    }

    @PutMapping("account/transaction/withdraw")
    public Mono<TransactionDto> withdraw(@RequestBody WithdrawDto withdrawDto){
        return transactionService.withdraw(withdrawDto);
    }

    @PostMapping("account/transaction/transfer")
    public Mono<TransactionDto> transfer(@RequestBody TransactionRequestDto transactionRequestDto) {
        return transactionService.wireTransfer(transactionRequestDto);
    }

    @GetMapping("account/transaction/{accountId}/list")
    public Flux<TransactionDto> listTransaction(@PathVariable("accountId") String accountId) {
        return transactionService.listTransactions(accountId);
    }
}
