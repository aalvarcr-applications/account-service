package com.nttdata.bootcamp.accountservice.application.mapper;

import com.nttdata.bootcamp.accountservice.model.dto.AccountDto;
import org.modelmapper.ModelMapper;

/**
 *
 * @since 2022
 * @param <T> Account
 */

public class MapperGeneric <T> {
    final Class<T> typeClass;
    ModelMapper modelMapper;
    public MapperGeneric(Class<T> typeClass){
        this.typeClass = typeClass;
        modelMapper = new ModelMapper();
    }

    public T toDto(AccountDto account) {
        return modelMapper.map(account, this.typeClass);
    }

    public AccountDto toAccountDto(T accountDto) {
        return modelMapper.map(accountDto, AccountDto.class);
    }
}
