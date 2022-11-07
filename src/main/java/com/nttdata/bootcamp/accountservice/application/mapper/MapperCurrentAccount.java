package com.nttdata.bootcamp.accountservice.application.mapper;

import com.nttdata.bootcamp.accountservice.model.CurrentAccount;
import com.nttdata.bootcamp.accountservice.model.dto.CurrentAccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @since 2022
 */

@Component
public class MapperCurrentAccount {
    public CurrentAccountDto toDto(CurrentAccount currentAccount) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(currentAccount, CurrentAccountDto.class);
    }

    public CurrentAccount toCurrentAccount(CurrentAccountDto currentAccountDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(currentAccountDto, CurrentAccount.class);
    }
}
