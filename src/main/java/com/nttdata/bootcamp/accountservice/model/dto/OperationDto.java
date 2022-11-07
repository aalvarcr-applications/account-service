package com.nttdata.bootcamp.accountservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;


/**
 *
 * @since 2022
 */
@Data
public class OperationDto {
    private BigDecimal amount;
    private BigDecimal commission;
    private String sourceAccount;
    private String targetAccount;
    private String accountId;
    private String operation;
}
