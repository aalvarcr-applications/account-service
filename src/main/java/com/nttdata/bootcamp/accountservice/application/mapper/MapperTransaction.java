package com.nttdata.bootcamp.accountservice.application.mapper;

import com.nttdata.bootcamp.accountservice.model.Transaction;
import com.nttdata.bootcamp.accountservice.model.dto.TransactionDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @since 2022
 */

@Component
public class MapperTransaction {
    public TransactionDto toDto(Transaction transaction) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(transaction, TransactionDto.class);
    }

    public Transaction toTransaction(TransactionDto transactionDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(transactionDto, Transaction.class);
    }
}
