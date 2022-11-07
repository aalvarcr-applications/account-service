package com.nttdata.bootcamp.accountservice.model.dto;

import com.nttdata.bootcamp.accountservice.model.constant.TypeCustomer;
import com.nttdata.bootcamp.accountservice.model.constant.TypeProfile;
import lombok.*;

/**
 *
 * @since 2022
 */
@Data
public class CustomerDto {
    private String id;
    private String name;
    private String lastName;
    private String businessName;
    private String emailAddress;
    private String numberDocument;
    private String status;
    private TypeCustomer typeCustomer;
    private TypeProfile typeProfile;
    private boolean itsVip;
    private boolean itsPyme;
    private boolean itsCompany;
    private boolean itsPersonal;
}