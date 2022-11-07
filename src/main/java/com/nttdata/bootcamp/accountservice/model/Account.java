package com.nttdata.bootcamp.accountservice.model;

import com.nttdata.bootcamp.accountservice.model.constant.StateAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 *
 * @since 2022
 */
@Document(collection = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private String id;
    private String number;
    private BigDecimal balance;
    private Integer dayOfOperation;
    private String coin;
    private String holderId;
    private List<String> holders;
    private List<String> authorizedSigners;
    private String productId;
    private Date createdAt;
    private Date updatedAt;
    private StateAccount state;
    private TypeAccount typeAccount;
}
