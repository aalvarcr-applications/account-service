package com.nttdata.bootcamp.accountservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @since 2022
 */
@Data
public class WithdrawDto {
    private BigDecimal amount;
    private String accountNumber;
    private String operation;
}
