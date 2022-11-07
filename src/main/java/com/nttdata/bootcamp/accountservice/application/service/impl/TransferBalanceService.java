package com.nttdata.bootcamp.accountservice.application.service.impl;

import com.nttdata.bootcamp.accountservice.application.exception.TransactionException;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperTransaction;
import com.nttdata.bootcamp.accountservice.infrastructure.*;
import com.nttdata.bootcamp.accountservice.model.Transaction;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAffectation;
import com.nttdata.bootcamp.accountservice.model.constant.TypeTransaction;
import com.nttdata.bootcamp.accountservice.model.dto.OperationDto;
import com.nttdata.bootcamp.accountservice.model.dto.TransactionDto;
import com.nttdata.bootcamp.accountservice.model.dto.TransactionRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 *
 * @since 2022
 */
@Service
public class TransferBalanceService {
  @Autowired
  TransactionRepository transactionRepository;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  MapperTransaction mapperTransaction;
  public Mono<TransactionDto> balanceToTargetAccount(TransactionRequestDto transferDto) {
    return accountRepository.findByNumber(transferDto.getTargetAccount())
        .switchIfEmpty(Mono.error(new TransactionException("Account destination does not exist")))
        .flatMap(account -> {
          account.setUpdatedAt(new Date());
          account.setBalance(account.getBalance().add(transferDto.getAmount()));
          return accountRepository.save(account);
        })
        .flatMap(rs-> {
          OperationDto operationDto = new OperationDto();
          operationDto.setAmount(transferDto.getAmount());
          operationDto.setAccountId(rs.getId());
          operationDto.setSourceAccount(transferDto.getSourceAccount());
          operationDto.setTargetAccount(transferDto.getTargetAccount());
          operationDto.setOperation(transferDto.getOperation());
          return this.saveTransaction(operationDto, TypeTransaction.ENTRY_TRANSFER,
            TypeAffectation.INCREASE, rs.getTypeAccount());
        });
  }
  public Mono<TransactionDto> saveTransaction(OperationDto operationDto,
                                              TypeTransaction typeTxn,
                                              TypeAffectation typeAffectation,
                                              TypeAccount typeAccount){
    Transaction transaction = new Transaction();
    transaction.setAccountId(operationDto.getAccountId());
    transaction.setSourceAccount(operationDto.getSourceAccount());
    transaction.setTargetAccount(operationDto.getTargetAccount());
    transaction.setDate(new Date());
    transaction.setAmount(operationDto.getAmount());
    transaction.setType(typeTxn);
    transaction.setOperation(operationDto.getOperation());
    transaction.setAffectation(typeAffectation);
    transaction.setTypeAccount(typeAccount);
    transaction.setCommission(operationDto.getCommission());
    return transactionRepository.insert(transaction)
        .map(mapperTransaction::toDto);
  }
}
