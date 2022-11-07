package com.nttdata.bootcamp.accountservice.model.dto;

import com.nttdata.bootcamp.accountservice.model.constant.StateTransaction;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @since 2022
 */
@Data
public class TransactionBootcoinDto {
  private String sourceNumber;
  private String targetNumber;
  private BigDecimal amount;
  private String transactionId;
  private StateTransaction state;
  private String detail;

}
