package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.exception.InsufficientBalanceException;
import com.nttdata.bootcamp.accountservice.application.exception.TransactionException;
import com.nttdata.bootcamp.accountservice.application.helper.DateUtil;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperTransaction;
import com.nttdata.bootcamp.accountservice.application.service.TransactionFixedTermAccountService;
import com.nttdata.bootcamp.accountservice.infrastructure.FixedTermAccountRepository;
import com.nttdata.bootcamp.accountservice.infrastructure.TransactionRepository;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.ProductClientService;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAffectation;
import com.nttdata.bootcamp.accountservice.model.constant.TypeTransaction;
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
public class TransactionFixedTermAccountServiceImpl
  implements TransactionFixedTermAccountService<FixedTermAccountDto> {
  @Autowired
  ProductClientService productClient;
  @Autowired
  FixedTermAccountRepository fixedTermAccountRepository;
  @Autowired
  TransactionRepository transactionRepository;
  @Autowired
  TransferBalanceService transferBalanceService;
  @Autowired
  MapperTransaction mapperTransaction;

  @Override
  public Mono<TransactionDto> deposit(DepositDto depositDto) {
    return fixedTermAccountRepository.findByNumberAndTypeAccount(depositDto.getTargetAccount(),
      TypeAccount.FIXED_TERM_ACCOUNT)
      .switchIfEmpty(Mono.error(new TransactionException(
      "Account number does not exist")))
      .doOnNext(fixedTermAccount -> {
      if (fixedTermAccount.getDayOfOperation() != DateUtil.getCurrentDay().intValue()) {
        throw new TransactionException(
        "Date not allowed to perform operations");
      }
      })
      .flatMap(fixedTermAccount -> {
      OperationDto operationDto = new OperationDto();
      operationDto.setAccountId(fixedTermAccount.getId());
      operationDto.setTargetAccount(depositDto.getTargetAccount());
      operationDto.setOperation(depositDto.getOperation());
      return productClient.getProductAccount(fixedTermAccount.getProductId())
        .switchIfEmpty(Mono.error(new TransactionException("Product does not exist")))
        .flatMap(productAccountDto-> transactionRepository
          .countByDateBetweenAndAccountIdAndTypeNot(DateUtil.getStartOfMonth(),
          DateUtil.getEndOfMonth(), fixedTermAccount.getId(),
          TypeTransaction.ENTRY_TRANSFER)

        .doOnNext(count -> {
          BigDecimal commission = BigDecimal.ZERO;
          if (productAccountDto.getMaxMovements() <= count) {
          commission = commission
            .add(BigDecimal.valueOf(productAccountDto.getCommissionAmount()));
          }
          operationDto.setCommission(commission);
        })
        .flatMap(count -> {
          BigDecimal totalAmount = fixedTermAccount.getBalance()
          .add(depositDto.getAmount()).subtract(operationDto.getCommission());

          fixedTermAccount.setUpdatedAt(new Date());
          fixedTermAccount.setBalance(totalAmount);
          return fixedTermAccountRepository.save(fixedTermAccount)
          .flatMap(ft -> {
            operationDto.setAmount(depositDto.getAmount().
            subtract(operationDto.getCommission()));
            return this.transferBalanceService
            .saveTransaction(operationDto, TypeTransaction.DEPOSIT,
              TypeAffectation.INCREASE, TypeAccount.FIXED_TERM_ACCOUNT);
          });
        }));
      });
  }
  @Override
  public Mono<TransactionDto> withdraw(WithdrawDto withdrawDto) {
    return fixedTermAccountRepository.findByNumberAndTypeAccount(withdrawDto.getAccountNumber(),
      TypeAccount.FIXED_TERM_ACCOUNT)
        .switchIfEmpty(Mono.error(new TransactionException(
          "Account number does not exist")))
        .doOnNext(fixedTermAccount -> {
          if (fixedTermAccount.getDayOfOperation() != 
            DateUtil.getCurrentDay().intValue()) {
            
            throw new TransactionException(
              "Date not allowed to perform operations");
          }
        })
        .flatMap(fixedTermAccount ->productClient.getProductAccount(fixedTermAccount.getProductId())
          .switchIfEmpty(Mono.error(new TransactionException(
          "Product does not exist")))
          .flatMap(productAccountDto -> {
          OperationDto operationDto = new OperationDto();
          operationDto.setAccountId(fixedTermAccount.getId());
          operationDto.setSourceAccount(withdrawDto.getAccountNumber());
          operationDto.setOperation(withdrawDto.getOperation());
          return transactionRepository
            .countByDateBetweenAndAccountIdAndTypeNot(DateUtil.getStartOfMonth(),
            DateUtil.getEndOfMonth(), fixedTermAccount.getId(),
            TypeTransaction.ENTRY_TRANSFER)
            .doOnNext(count -> {
            BigDecimal commission = BigDecimal.ZERO;
            if (productAccountDto.getMaxMovements() <= count) {
              commission = commission
                .add(BigDecimal.valueOf(productAccountDto.getCommissionAmount()));
            }
            BigDecimal totalAmount = withdrawDto.getAmount().add(commission);
            if (totalAmount.compareTo(fixedTermAccount.getBalance()) > 0) {
              throw new InsufficientBalanceException(
              "There is not enough balance to execute the operation");
            }
            operationDto.setCommission(commission);
            operationDto.setAmount(withdrawDto.getAmount().add(commission));
            fixedTermAccount.setUpdatedAt(new Date());
            fixedTermAccount.setBalance(fixedTermAccount.getBalance().subtract(totalAmount));
            }).flatMap(count -> fixedTermAccountRepository
            .save(fixedTermAccount)
            .flatMap(ft -> this.transferBalanceService.saveTransaction(operationDto,
              TypeTransaction.WITHDRAW, TypeAffectation.DECREMENT,
              TypeAccount.FIXED_TERM_ACCOUNT)));
          }));
  }
  @Override
  public Mono<TransactionDto> wireTransfer(TransactionRequestDto transferDto) {
    return fixedTermAccountRepository.findByNumberAndTypeAccount(transferDto.getSourceAccount(),
        TypeAccount.FIXED_TERM_ACCOUNT)
      .switchIfEmpty(Mono.error(
        new TransactionException("Account number does not exist")))
      .doOnNext(fixedTermAccount -> {
      if (fixedTermAccount.getDayOfOperation() != DateUtil.getCurrentDay().intValue()) {
        throw new TransactionException(
        "Date not allowed to perform operations");
      }
      if (transferDto.getAmount().compareTo(fixedTermAccount.getBalance()) > 0) {
        throw new InsufficientBalanceException(
        "There is not enough balance to execute the operation");
      }
      }).flatMap(fixedTermAccount -> {
      OperationDto operationDto = new OperationDto();
      operationDto.setAccountId(fixedTermAccount.getId());
      operationDto.setSourceAccount(transferDto.getSourceAccount());
      operationDto.setTargetAccount(transferDto.getTargetAccount());
      operationDto.setOperation(transferDto.getOperation());
      return productClient.getProductAccount(fixedTermAccount.getProductId())
        .switchIfEmpty(Mono.error(new TransactionException("Product does not exist")))
        .flatMap(productAccountDto -> {

        return transactionRepository.countByDateBetweenAndAccountIdAndTypeNot(
            DateUtil.getStartOfMonth(),
            DateUtil.getEndOfMonth(),
            fixedTermAccount.getId(),
            TypeTransaction.ENTRY_TRANSFER)

          .doOnNext(count -> {
          BigDecimal commission = BigDecimal.ZERO;
          if (productAccountDto.getMaxMovements() <= count) {
            commission = commission
              .add(BigDecimal.valueOf(productAccountDto.getCommissionAmount()));
          }
          BigDecimal totalAmount = transferDto.getAmount().add(commission);
          if (totalAmount.compareTo(fixedTermAccount.getBalance()) > 0) {
            throw new InsufficientBalanceException(
            "There is not enough balance to execute the operation");
          }
          BigDecimal finalCommission = commission;
          fixedTermAccount.setBalance(fixedTermAccount.getBalance().subtract(totalAmount));
          fixedTermAccount.setUpdatedAt(new Date());
          operationDto.setAmount(transferDto.getAmount().add(finalCommission));
          operationDto.setCommission(finalCommission);
          }).flatMap(count -> transferBalanceService.balanceToTargetAccount(transferDto)
          .flatMap(operationDto1 -> fixedTermAccountRepository.save(fixedTermAccount)
            .flatMap(ft-> this.transferBalanceService.saveTransaction(operationDto,
              TypeTransaction.OUTPUT_TRANSFER, TypeAffectation.DECREMENT,
              TypeAccount.FIXED_TERM_ACCOUNT))
          ));
        });
      });
  }

  @Override
  public Flux<TransactionDto> listTransactions(String accountId) {
    return transactionRepository.findByAccountIdAndTypeAccount(accountId,
        TypeAccount.FIXED_TERM_ACCOUNT)
        .map(mapperTransaction::toDto);
  }
}
