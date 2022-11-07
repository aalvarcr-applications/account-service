package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.exception.InsufficientBalanceException;
import com.nttdata.bootcamp.accountservice.application.exception.TransactionException;
import com.nttdata.bootcamp.accountservice.application.helper.DateUtil;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperTransaction;
import com.nttdata.bootcamp.accountservice.application.service.TransactionSavingsAccountService;
import com.nttdata.bootcamp.accountservice.infrastructure.*;
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
public class TransactionSavingsAccountServiceImpl 
  implements TransactionSavingsAccountService<SavingsAccountDto> {
  @Autowired
  ProductClientService productClient;
  @Autowired
  SavingsAccountRepository savingRepository;
  @Autowired
  TransactionRepository transRepository;
  @Autowired
  TransferBalanceService transferBalanceService;
  @Autowired
  MapperTransaction mapperTransaction;
  @Override
  public Mono<TransactionDto> deposit(DepositDto depositDto) {
    return savingRepository.findByNumberAndTypeAccount(depositDto.getTargetAccount(),
        TypeAccount.SAVINGS_ACCOUNT)
        .switchIfEmpty(Mono.error(new TransactionException("Account number does not exist")))
        .flatMap(savingsAccount -> {
          OperationDto operationDto = new OperationDto();
          operationDto.setAccountId(savingsAccount.getId());
          operationDto.setTargetAccount(depositDto.getTargetAccount());
          operationDto.setOperation(depositDto.getOperation());
          return productClient.getProductAccount(savingsAccount.getProductId())
              .switchIfEmpty(Mono.error(new TransactionException("Product does not exist")))
              .flatMap(productAccountDto -> {

                return transRepository.countByDateBetweenAndAccountIdAndTypeNot(
                    DateUtil.getStartOfMonth(),
                    DateUtil.getEndOfMonth(),
                    savingsAccount.getId(),
                    TypeTransaction.ENTRY_TRANSFER)

                    .doOnNext(count -> {
                      BigDecimal commission = BigDecimal.ZERO;
                      if (productAccountDto.getMaxMovements() <= count) {
                        commission = commission
                          .add(BigDecimal.valueOf(productAccountDto.getCommissionAmount()));
                      }
                      savingsAccount.setBalance(savingsAccount.getBalance()
                        .add(depositDto.getAmount()).subtract(commission));
                      savingsAccount.setUpdatedAt(new Date());
                      operationDto.setAmount(depositDto.getAmount()
                        .subtract(commission));
                      operationDto.setCommission(commission);
                    })
                    .flatMap(count -> {
                      return savingRepository.save(savingsAccount)
                          .flatMap(sa -> this.transferBalanceService.saveTransaction(operationDto,
                            TypeTransaction.DEPOSIT, TypeAffectation.INCREASE,
                            TypeAccount.SAVINGS_ACCOUNT));
                    });
              });
        });
  }
  @Override
  public Mono<TransactionDto> withdraw(WithdrawDto withdrawDto) {
    return savingRepository.findByNumberAndTypeAccount(withdrawDto.getAccountNumber(),
        TypeAccount.SAVINGS_ACCOUNT)
        .switchIfEmpty(Mono.error(new TransactionException("Account number does not exist")))
        .flatMap(savingsAccount -> {
          OperationDto operationDto = new OperationDto();
          operationDto.setAccountId(savingsAccount.getId());
          operationDto.setSourceAccount(withdrawDto.getAccountNumber());
          operationDto.setOperation(withdrawDto.getOperation());
          return productClient.getProductAccount(savingsAccount.getProductId())
              .switchIfEmpty(Mono.error(new TransactionException("Product does not exist")))
              .flatMap(productAccountDto -> {
                return transRepository.countByDateBetweenAndAccountIdAndTypeNot(
                  DateUtil.getStartOfMonth(),
                    DateUtil.getEndOfMonth(),
                    savingsAccount.getId(),
                    TypeTransaction.ENTRY_TRANSFER)
                    .doOnNext(count -> {
                      BigDecimal commission = BigDecimal.ZERO;
                      if (productAccountDto.getMaxMovements() <= count) {
                        commission = commission
                          .add(BigDecimal.valueOf(productAccountDto.getCommissionAmount()));
                      }
                      BigDecimal totalAmount = withdrawDto.getAmount().add(commission);
                      if (totalAmount.compareTo(savingsAccount.getBalance()) > 0) {
                        throw new InsufficientBalanceException(
                          "There is not enough balance to execute the operation");
                      }
                      savingsAccount.setBalance(savingsAccount.getBalance().subtract(totalAmount));
                      savingsAccount.setUpdatedAt(new Date());
                      operationDto.setAmount(withdrawDto.getAmount().add(commission));
                      operationDto.setCommission(commission);
                    })
                    .flatMap(count-> {
                      return savingRepository.save(savingsAccount)
                          .flatMap(sa -> this.transferBalanceService.saveTransaction(operationDto,
                            TypeTransaction.WITHDRAW, TypeAffectation.DECREMENT,
                            TypeAccount.SAVINGS_ACCOUNT));
                    });
                });
              });
  }
  @Override
  public Mono<TransactionDto> wireTransfer(TransactionRequestDto transferDto) {
    return savingRepository.findByNumberAndTypeAccount(transferDto.getSourceAccount(),
        TypeAccount.SAVINGS_ACCOUNT)
        .switchIfEmpty(Mono.error(new TransactionException("Account number does not exist")))
        .flatMap(savingsAccount -> {
          OperationDto operationDto = new OperationDto();
          operationDto.setAccountId(savingsAccount.getId());
          operationDto.setSourceAccount(transferDto.getSourceAccount());
          operationDto.setTargetAccount(transferDto.getTargetAccount());
          operationDto.setOperation(transferDto.getOperation());
          return productClient.getProductAccount(savingsAccount.getProductId())
              .switchIfEmpty(Mono.error(new TransactionException("Product does not exist")))
              .flatMap(productAccountDto -> {
                return transRepository.countByDateBetweenAndAccountIdAndTypeNot(
                  DateUtil.getStartOfMonth(), DateUtil.getEndOfMonth(), savingsAccount.getId(),
                    TypeTransaction.ENTRY_TRANSFER)

                    .doOnNext(count -> {
                      BigDecimal commission = BigDecimal.ZERO;
                      if (productAccountDto.getMaxMovements() <= count) {
                        commission = commission.add(BigDecimal
                          .valueOf(productAccountDto.getCommissionAmount()));
                      }
                      BigDecimal totalAmount = transferDto.getAmount().add(commission);
                      if (totalAmount.compareTo(savingsAccount.getBalance()) > 0) {
                        throw new InsufficientBalanceException(
                          "There is not enough balance to execute the operation");
                      }
                      BigDecimal finalCommission = commission;
                      savingsAccount.setBalance(savingsAccount.getBalance().subtract(totalAmount));
                      savingsAccount.setUpdatedAt(new Date());
                      operationDto.setAmount(transferDto.getAmount().add(finalCommission));
                      operationDto.setCommission(finalCommission);
                    })
                    .flatMap(count-> {
                      return this.transferBalanceService.balanceToTargetAccount(transferDto)
                          .flatMap(operationDto1 -> {
                            return savingRepository.save(savingsAccount)
                                .flatMap(rs -> this.transferBalanceService
                                  .saveTransaction(operationDto, TypeTransaction.OUTPUT_TRANSFER,
                                    TypeAffectation.DECREMENT, TypeAccount.SAVINGS_ACCOUNT));
                          });
                      });
              });
        });
  }

  @Override
  public Flux<TransactionDto> listTransactions(String accountId) {
    return transRepository.findByAccountIdAndTypeAccount(accountId, TypeAccount.SAVINGS_ACCOUNT)
        .map(mapperTransaction::toDto);
  }
}
