package com.nttdata.bootcamp.accountservice.application.mapper;

import com.nttdata.bootcamp.accountservice.model.Account;
import com.nttdata.bootcamp.accountservice.model.dto.AccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @since 2022
 */
@Component
public class MapperAccount {
    public AccountDto toDto(Account account) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(account, AccountDto.class);
    }

    public Account toAccount(AccountDto accountDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(accountDto, Account.class);
    }
}
