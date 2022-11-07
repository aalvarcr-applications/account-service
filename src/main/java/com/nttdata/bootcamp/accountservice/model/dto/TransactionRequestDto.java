package com.nttdata.bootcamp.accountservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @since 2022
 */
@Builder
@Data
public class TransactionRequestDto {
    private BigDecimal amount;
    private String sourceAccount;
    private String targetAccount;
    private String operation;
}
