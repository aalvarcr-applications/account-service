package com.nttdata.bootcamp.accountservice.application.mapper;

import com.nttdata.bootcamp.accountservice.model.FixedTermAccount;
import com.nttdata.bootcamp.accountservice.model.dto.FixedTermAccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @since 2022
 */

@Component
public class MapperFixedTerm {
    public FixedTermAccountDto toDto(FixedTermAccount fixedTermAccount) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(fixedTermAccount, FixedTermAccountDto.class);
    }

    public FixedTermAccount toProductAccount(FixedTermAccountDto fixedTermAccountDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(fixedTermAccountDto, FixedTermAccount.class);
    }
}
