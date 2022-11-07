package com.nttdata.bootcamp.accountservice.model.dto;

import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAffectation;
import com.nttdata.bootcamp.accountservice.model.constant.TypeTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @since 2022
 */
@Data
public class TransactionDto {
    private String id;
    private String operation;
    private BigDecimal amount;
    private BigDecimal commission;
    private String accountId;
    private String sourceAccount;
    private String targetAccount;
    private Date date;
    private TypeTransaction type;
    private TypeAffectation affectation;
    private TypeAccount typeAccount;
}
