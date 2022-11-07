package com.nttdata.bootcamp.accountservice.service.impl;

import com.nttdata.bootcamp.accountservice.application.mapper.MapperCurrentAccount;
import com.nttdata.bootcamp.accountservice.application.service.CurrentAccountService;
import com.nttdata.bootcamp.accountservice.application.service.impl.CurrentAccountServiceImpl;
import com.nttdata.bootcamp.accountservice.infrastructure.CurrentAccountRepository;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.CreditClientService;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.CustomerClientService;
import com.nttdata.bootcamp.accountservice.infrastructure.feignclient.ProductClientService;
import com.nttdata.bootcamp.accountservice.model.CurrentAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeCustomer;
import com.nttdata.bootcamp.accountservice.model.constant.TypeProfile;
import com.nttdata.bootcamp.accountservice.model.dto.CreditCardDto;
import com.nttdata.bootcamp.accountservice.model.dto.CurrentAccountDto;
import com.nttdata.bootcamp.accountservice.model.dto.CustomerDto;
import com.nttdata.bootcamp.accountservice.model.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class CurrentAccountServiceImplTest {

    @TestConfiguration
    static class CurrentAccountServiceImplTestConfiguration {
        @Bean
        public CurrentAccountService<CurrentAccountDto> currentAccountService() {
            return new CurrentAccountServiceImpl();
        }

        @Bean
        public MapperCurrentAccount mapperCurrentAccount(){
            return new MapperCurrentAccount();
        }

        @MockBean
        public CustomerClientService customerClient;
        @MockBean
        public ProductClientService productClient;
        @MockBean
        public CreditClientService creditClient;
        @MockBean
        public CurrentAccountRepository currentAccountRepository;
    }

    @Autowired
    CurrentAccountService<CurrentAccountDto> currentAccountService;
    @Autowired
    CustomerClientService customerClient;
    @Autowired
    ProductClientService productClient;
    @Autowired
    CreditClientService creditClient;
    @Autowired
    CurrentAccountRepository currentAccountRepository;

    @Test
    @DisplayName("should validate the creation of an current account")
    void create() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId("6359e98680b99b212dbf0b40");
        customerDto.setName("Angel");
        customerDto.setLastName("Alvarado");
        customerDto.setEmailAddress("angeljoel.1218@gmail.com");
        customerDto.setNumberDocument("46752852");
        customerDto.setTypeCustomer(TypeCustomer.PERSONAL);
        customerDto.setTypeProfile(TypeProfile.VIP);

        List<String> holders = new ArrayList<>();
        holders.add("6359e98680b99b212dbf0b40");
        List<String> signatures = new ArrayList<>();
        signatures.add("Anonymous");
        CurrentAccountDto currentAccountDto = new CurrentAccountDto();
        currentAccountDto.setTypeAccount(TypeAccount.CURRENT_ACCOUNT);
        currentAccountDto.setNumber("11564889845335");
        currentAccountDto.setBalance(BigDecimal.ZERO);
        currentAccountDto.setHolderId("6359e98680b99b212dbf0b40");
        currentAccountDto.setHolders(holders);
        currentAccountDto.setProductId("63596a9a3e79080adc09ae4a");
        currentAccountDto.setCoin("PEN");
        currentAccountDto.setAuthorizedSigners(signatures);

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setId("635a10b7a8725332918df4d8");
        creditCardDto.setIdProduct("6359db1ce2e7572b4c0be55c");
        creditCardDto.setIdCustomer("6359e98680b99b212dbf0b40");
        creditCardDto.setLimitAmount(new BigDecimal(500));

        ProductDto productDto = new ProductDto();
        productDto.setId("63596a9a3e79080adc09ae4a");
        productDto.setCode("002");
        productDto.setName("Cuenta corriente");
        productDto.setMaintenance(10F);
        productDto.setMaxNumberCredits(0);
        productDto.setCommissionAmount(15F);
        productDto.setOpeningAmount(0F);
        productDto.setMinFixedAmount(0F);
        productDto.setProductTypeId(TypeAccount.CURRENT_ACCOUNT);

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId("fghjklasaaaaaaaaaaaaaaa");
        currentAccount.setTypeAccount(TypeAccount.CURRENT_ACCOUNT);
        currentAccount.setNumber("11564889845335");
        currentAccount.setBalance(BigDecimal.ZERO);
        currentAccount.setHolderId("6359e98680b99b212dbf0b40");
        currentAccount.setHolders(holders);
        currentAccount.setProductId("63596a9a3e79080adc09ae4a");
        currentAccount.setCoin("PEN");
        currentAccount.setAuthorizedSigners(signatures);

        when(customerClient.getCustomer("6359e98680b99b212dbf0b40")).thenReturn(Mono.just(customerDto));

        when(creditClient.getCreditCardCustomer("6359e98680b99b212dbf0b40")).thenReturn(Flux.just(creditCardDto));

        when(currentAccountRepository.countByHolderId("6359e98680b99b212dbf0b40")).thenReturn(Mono.just(0L));

        when(productClient.getProductAccount("63596a9a3e79080adc09ae4a")).thenReturn(Mono.just(productDto));

        when(currentAccountRepository.countByNumber("11564889845335")).thenReturn(Mono.just(0L));

        when(currentAccountRepository.insert(currentAccount)).thenReturn(Mono.just(currentAccount));

        StepVerifier.create(currentAccountService.create(currentAccountDto))
                .expectNext()
                .consumeErrorWith(throwable -> {
                    System.out.println("=========>"+throwable.getMessage());
                });
    }
}
