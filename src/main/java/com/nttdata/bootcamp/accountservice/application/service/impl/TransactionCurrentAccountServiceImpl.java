package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.exception.InsufficientBalanceException;
import com.nttdata.bootcamp.accountservice.application.exception.TransactionException;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperTransaction;
import com.nttdata.bootcamp.accountservice.application.service.TransactionCurrentAccountService;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.CustomerClientService;
import com.nttdata.bootcamp.accountservice.infrastructure.CurrentAccountRepository;
import com.nttdata.bootcamp.accountservice.infrastructure.TransactionRepository;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.ProductClientService;
import com.nttdata.bootcamp.accountservice.model.*;
import com.nttdata.bootcamp.accountservice.model.constant.*;
import com.nttdata.bootcamp.accountservice.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @since 2022
 */

@Service
public class TransactionCurrentAccountServiceImpl
  implements TransactionCurrentAccountService<CurrentAccountDto> {
    @Autowired
    CustomerClientService customerClient;
    @Autowired
    ProductClientService productClient;
    @Autowired
    CurrentAccountRepository currentAccountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransferBalanceService transferBalanceService;
    @Autowired
    MapperTransaction mapperTransaction;

    @Override
    public Mono<TransactionDto> deposit(DepositDto depositDto) {
        return currentAccountRepository.findByNumberAndTypeAccount(depositDto.getTargetAccount(),
            TypeAccount.CURRENT_ACCOUNT)
                .switchIfEmpty(Mono.error(
                  new TransactionException(Constants.ACCOUNT_NOT_EXIST)))
                .flatMap(currentAccount -> {
                    currentAccount.setBalance(currentAccount.getBalance()
                      .add(depositDto.getAmount()));
                    currentAccount.setUpdatedAt(new Date());
                    return currentAccountRepository.save(currentAccount)
                            .flatMap(ca -> {
                                OperationDto operationDto = new OperationDto();
                                operationDto.setAmount(depositDto.getAmount());
                                operationDto.setTargetAccount(depositDto.getTargetAccount());
                                operationDto.setOperation(depositDto.getOperation());
                                operationDto.setAccountId(ca.getId());
                                return this.transferBalanceService.saveTransaction(operationDto,
                                  TypeTransaction.DEPOSIT, TypeAffectation.INCREASE,
                                  TypeAccount.CURRENT_ACCOUNT);
                            });
                });
    }
    @Override
    public Mono<TransactionDto> withdraw(WithdrawDto withdrawDto) {
        return currentAccountRepository.findByNumberAndTypeAccount(withdrawDto.getAccountNumber(),
            TypeAccount.CURRENT_ACCOUNT)
          .switchIfEmpty(Mono.error(new TransactionException(Constants.ACCOUNT_NOT_EXIST)))
          .doOnNext(currentAccount -> {
            if (withdrawDto.getAmount().compareTo(currentAccount.getBalance()) > 0 ) {
              throw new InsufficientBalanceException(
                "There is not enough balance to execute the operation");
            }
          }).flatMap(currentAccount -> customerClient.getCustomer(currentAccount.getHolderId())
            .switchIfEmpty(Mono.error(new TransactionException("Customer not found")))
            .flatMap(customerDto -> {
              if (customerDto.getTypeProfile() != null
                && customerDto.getTypeProfile().equals(TypeProfile.VIP)) {
                return productClient.getProductAccount(currentAccount.getProductId())
                  .switchIfEmpty(Mono.error(new TransactionException(
                    "Product not found")))
                  .doOnNext(productDto -> {
                    BigDecimal minFixedAmount = currentAccount.getBalance()
                      .subtract(withdrawDto.getAmount());
                    if (minFixedAmount.compareTo(BigDecimal.
                      valueOf(productDto.getMinFixedAmount())) < 0 ) {
                      throw new InsufficientBalanceException(
                        "Error the account requires a minimum amount of balance allowed");
                    }
                  })
                  .flatMap(productDto -> this.execWithdraw(currentAccount, withdrawDto));
              }
              return this.execWithdraw(currentAccount, withdrawDto);
            }));
    }
    @Override
    public Mono<TransactionDto> wireTransfer(TransactionRequestDto transferDto) {
        return currentAccountRepository.findByNumberAndTypeAccount(transferDto.getSourceAccount(),
                TypeAccount.CURRENT_ACCOUNT)
          .switchIfEmpty(Mono.error(new TransactionException(Constants.ACCOUNT_NOT_EXIST)))
          .doOnNext(currentAccount -> {
            if (transferDto.getAmount().compareTo(currentAccount.getBalance()) > 0 ) {
                        throw new InsufficientBalanceException("There is not enough balance" +
                          " to execute the operation");
            }
          })
          .flatMap(currentAccount -> {
            OperationDto operationDto = new OperationDto();
            operationDto.setAccountId(currentAccount.getId());
            operationDto.setAmount(transferDto.getAmount());
            operationDto.setSourceAccount(transferDto.getSourceAccount());
            operationDto.setTargetAccount(transferDto.getTargetAccount());
            operationDto.setOperation(transferDto.getOperation());
            return customerClient.getCustomer(currentAccount.getHolderId())
              .switchIfEmpty(Mono.error(new TransactionException("Client does not exist")))
              .flatMap(customerDto -> {
                if (customerDto.getTypeProfile() != null
                  && customerDto.getTypeProfile().equals(TypeProfile.VIP)) {
                  return productClient.getProductAccount(currentAccount.getProductId())
                    .switchIfEmpty(Mono.error(new TransactionException("Product not found")))
                    .doOnNext(productDto -> {
                      BigDecimal minFixedAmount = currentAccount.getBalance()
                        .subtract(transferDto.getAmount());
                      if (minFixedAmount.compareTo
                        (BigDecimal.valueOf(productDto.getMinFixedAmount())) < 0 ) {

                        throw new InsufficientBalanceException(
                          "Error the account requires a minimum amount of balance allowed");
                      }})
                    .flatMap(productDto ->
                      this.transferBalanceService.balanceToTargetAccount(transferDto)
                      .flatMap(txn -> {

                        currentAccount.setBalance(currentAccount.getBalance()
                          .subtract(transferDto.getAmount()));

                        currentAccount.setUpdatedAt(new Date());
                        return currentAccountRepository.save(currentAccount)
                          .flatMap(rs -> this.transferBalanceService
                            .saveTransaction(operationDto, TypeTransaction.OUTPUT_TRANSFER,
                              TypeAffectation.DECREMENT, TypeAccount.SAVINGS_ACCOUNT));
                      }));
                }
                return this.transferBalanceService.balanceToTargetAccount(transferDto)
                  .flatMap(txn -> {
                    currentAccount.setBalance(currentAccount.getBalance()
                      .subtract(transferDto.getAmount()));
                    currentAccount.setUpdatedAt(new Date());
                    return currentAccountRepository.save(currentAccount)
                      .flatMap(rs -> this.transferBalanceService.saveTransaction(operationDto,
                        TypeTransaction.OUTPUT_TRANSFER, TypeAffectation.DECREMENT,
                        TypeAccount.SAVINGS_ACCOUNT));
                  });
              });
          });
    }
    @Override
    public Flux<TransactionDto> listTransactions(String accountId) {
        return transactionRepository.findByAccountIdAndTypeAccount(accountId,
            TypeAccount.CURRENT_ACCOUNT)
                .map(mapperTransaction::toDto);
    }

    public Mono<TransactionDto> execWithdraw(CurrentAccount currentAccount,
                                             WithdrawDto withdrawDto) {
        currentAccount.setBalance(currentAccount.getBalance()
          .subtract(withdrawDto.getAmount()));
        currentAccount.setUpdatedAt(new Date());
        return currentAccountRepository.save(currentAccount)
                .flatMap(ca -> {
                    OperationDto operationDto = new OperationDto();
                    operationDto.setAmount(withdrawDto.getAmount());
                    operationDto.setAccountId(ca.getId());
                    operationDto.setSourceAccount(withdrawDto.getAccountNumber());
                    operationDto.setOperation(withdrawDto.getOperation());
                    return this.transferBalanceService.saveTransaction(operationDto,
                      TypeTransaction.WITHDRAW, TypeAffectation.DECREMENT,
                      TypeAccount.CURRENT_ACCOUNT);
                });
    }
}
