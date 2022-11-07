package com.nttdata.bootcamp.accountservice.model.dto;

import com.nttdata.bootcamp.accountservice.model.constant.StateAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 *
 * @since 2022
 */
@Data
public class SavingsAccountDto {
    private String id;
    private String number;
    private BigDecimal balance;
    private String coin;
    private String holderId;
    private String productId;
    private Date createdAt;
    private Date updatedAt;
    private StateAccount state;
    private TypeAccount typeAccount;
}
