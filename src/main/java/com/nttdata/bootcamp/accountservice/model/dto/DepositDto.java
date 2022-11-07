package com.nttdata.bootcamp.accountservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;


/**
 *
 * @since 2022
 */
@Data
public class DepositDto {
    private BigDecimal amount;
    private String targetAccount;
    private String operation;
}
