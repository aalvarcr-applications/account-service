package com.nttdata.bootcamp.accountservice.application.mapper;

import com.nttdata.bootcamp.accountservice.model.SavingsAccount;
import com.nttdata.bootcamp.accountservice.model.dto.SavingsAccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @since 2022
 */

@Component
public class MapperSavingsAccount {
    public SavingsAccountDto toDto(SavingsAccount savingsAccount) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(savingsAccount, SavingsAccountDto.class);
    }

    public SavingsAccount toSavingsAccount(SavingsAccountDto savingsAccountDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(savingsAccountDto, SavingsAccount.class);
    }
}
