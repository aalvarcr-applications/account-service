package com.nttdata.bootcamp.accountservice.controller;

import com.nttdata.bootcamp.accountservice.application.controller.AccountController;
import com.nttdata.bootcamp.accountservice.application.mapper.MapperAccount;
import com.nttdata.bootcamp.accountservice.application.service.AccountService;
import com.nttdata.bootcamp.accountservice.application.service.CurrentAccountService;
import com.nttdata.bootcamp.accountservice.application.service.FixedTermAccountService;
import com.nttdata.bootcamp.accountservice.application.service.SavingsAccountService;
import com.nttdata.bootcamp.accountservice.application.service.impl.AccountServiceImpl;
import com.nttdata.bootcamp.accountservice.infrastructure.AccountRepository;
import com.nttdata.bootcamp.accountservice.model.Account;
import com.nttdata.bootcamp.accountservice.model.constant.StateAccount;
import com.nttdata.bootcamp.accountservice.model.constant.TypeAccount;
import com.nttdata.bootcamp.accountservice.model.dto.AccountDto;
import com.nttdata.bootcamp.accountservice.model.dto.CurrentAccountDto;
import com.nttdata.bootcamp.accountservice.model.dto.FixedTermAccountDto;
import com.nttdata.bootcamp.accountservice.model.dto.SavingsAccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@WebFluxTest(controllers = AccountController.class)
@ExtendWith(SpringExtension.class)
public class AccountControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    SavingsAccountService<SavingsAccountDto> savingsAccountService;

    @MockBean
    CurrentAccountService<CurrentAccountDto> currentAccountService;

    @MockBean
    FixedTermAccountService<FixedTermAccountDto> fixedTermAccountService;

    @MockBean
    AccountRepository accountRepository;

    @Test
    @DisplayName("should validate the creation of an savings account")
    void createSavingsAccount(){
        AccountDto accountDto = new AccountDto();
        accountDto.setTypeAccount(TypeAccount.SAVINGS_ACCOUNT);
        accountDto.setBalance(BigDecimal.ZERO);
        accountDto.setNumber("11564889845335");
        accountDto.setHolderId("635a098080b99b212dbf0b42");
        accountDto.setProductId("63596a9a3e79080adc09ae4a");
        accountDto.setCoin("PEN");
        accountDto.setState(StateAccount.ACTIVE);

        SavingsAccountDto savingsAccountDto = new SavingsAccountDto();
        savingsAccountDto.setTypeAccount(accountDto.getTypeAccount());
        savingsAccountDto.setNumber(accountDto.getNumber());
        savingsAccountDto.setBalance(accountDto.getBalance());
        savingsAccountDto.setHolderId(accountDto.getHolderId());
        savingsAccountDto.setProductId(accountDto.getProductId());
        savingsAccountDto.setCoin(accountDto.getCoin());
        savingsAccountDto.setState(StateAccount.ACTIVE);

        when(savingsAccountService.create(savingsAccountDto)).thenReturn(Mono.just(savingsAccountDto));

        this.webTestClient
                .post()
                .uri("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(accountDto), AccountDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountDto.class);
    }

    @Test
    @DisplayName("Should validate the creation of an current account")
    void createCurrentAccount() {
        List<String> holders = new ArrayList<>();
        holders.add("635a098080b99b212dbf0b42");

        List<String> signatures = new ArrayList<>();
        signatures.add("Anonymous");

        AccountDto accountDto = new AccountDto();
        accountDto.setTypeAccount(TypeAccount.CURRENT_ACCOUNT);
        accountDto.setBalance(BigDecimal.ZERO);
        accountDto.setNumber("11564889845335");
        accountDto.setHolderId("635a098080b99b212dbf0b42");
        accountDto.setProductId("63596a9a3e79080adc09ae4a");
        accountDto.setHolders(holders);
        accountDto.setAuthorizedSigners(signatures);
        accountDto.setCoin("PEN");
        accountDto.setState(StateAccount.ACTIVE);

        CurrentAccountDto currentAccountDto = new CurrentAccountDto();
        currentAccountDto.setTypeAccount(accountDto.getTypeAccount());
        currentAccountDto.setNumber(accountDto.getNumber());
        currentAccountDto.setBalance(accountDto.getBalance());
        currentAccountDto.setHolderId(accountDto.getHolderId());

        currentAccountDto.setHolders(holders);
        currentAccountDto.setProductId(accountDto.getProductId());
        currentAccountDto.setCoin(accountDto.getCoin());

        currentAccountDto.setAuthorizedSigners(signatures);
        currentAccountDto.setState(StateAccount.ACTIVE);

        when(currentAccountService.create(currentAccountDto)).thenReturn(Mono.just(currentAccountDto));

        this.webTestClient
                .post()
                .uri("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(accountDto), AccountDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountDto.class);
    }

    @Test
    @DisplayName("Should validate the creation of an fixed term account")
    void createFixedTermAccount() {
        AccountDto accountDto = new AccountDto();
        accountDto.setTypeAccount(TypeAccount.FIXED_TERM_ACCOUNT);
        accountDto.setBalance(BigDecimal.ZERO);
        accountDto.setNumber("11564889845335");
        accountDto.setHolderId("635a098080b99b212dbf0b42");
        accountDto.setProductId("63596a9a3e79080adc09ae4a");
        accountDto.setDayOfOperation(20);
        accountDto.setCoin("PEN");
        accountDto.setState(StateAccount.ACTIVE);

        FixedTermAccountDto fixedTermAccountDto = new FixedTermAccountDto();
        fixedTermAccountDto.setTypeAccount(accountDto.getTypeAccount());
        fixedTermAccountDto.setNumber(accountDto.getNumber());
        fixedTermAccountDto.setBalance(accountDto.getBalance());
        fixedTermAccountDto.setHolderId(accountDto.getHolderId());
        fixedTermAccountDto.setProductId(accountDto.getProductId());
        fixedTermAccountDto.setDayOfOperation(20);
        fixedTermAccountDto.setCoin(accountDto.getCoin());
        fixedTermAccountDto.setState(StateAccount.ACTIVE);

        when(fixedTermAccountService.create(fixedTermAccountDto)).thenReturn(Mono.just(fixedTermAccountDto));

        this.webTestClient
                .post()
                .uri("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(accountDto), AccountDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountDto.class);
    }

    @Test
    @DisplayName("It should return the data of an account")
    void findByNumber(){
        AccountDto accountDto = new AccountDto();
        accountDto.setBalance(new BigDecimal(100));
        accountDto.setNumber("11564889845335");
        accountDto.setHolderId("635a098080b99b212dbf0b42");
        accountDto.setProductId("63596a9a3e79080adc09ae4a");
        accountDto.setCoin("PEN");
        accountDto.setState(StateAccount.ACTIVE);
        accountDto.setCreatedAt(new Date());

        Account account = new Account();
        account.setBalance(accountDto.getBalance());
        account.setNumber(accountDto.getNumber());
        account.setHolderId(accountDto.getHolderId());
        account.setProductId(accountDto.getProductId());
        account.setCoin(accountDto.getCoin());
        account.setState(accountDto.getState());
        account.setCreatedAt(accountDto.getCreatedAt());

        when(accountRepository.findByNumber("11564889845335")).thenReturn(Mono.just(account));

        var responseFlux= webTestClient.get().uri("/account/11564889845335")
                .exchange()
                .expectStatus().isOk()
                .returnResult(AccountDto.class)
                .getResponseBody();


        StepVerifier.create(responseFlux)
                .expectSubscription()
                .expectNext(accountDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("I should return the accounts of a holder")
    void findByHolderId() {
        AccountDto accountDto = new AccountDto();
        accountDto.setBalance(new BigDecimal(100));
        accountDto.setNumber("11564889845335");
        accountDto.setHolderId("635a098080b99b212dbf0b42");
        accountDto.setProductId("63596a9a3e79080adc09ae4a");
        accountDto.setCoin("PEN");
        accountDto.setState(StateAccount.ACTIVE);
        accountDto.setCreatedAt(new Date());

        Account account = new Account();
        account.setBalance(accountDto.getBalance());
        account.setNumber(accountDto.getNumber());
        account.setHolderId(accountDto.getHolderId());
        account.setProductId(accountDto.getProductId());
        account.setCoin(accountDto.getCoin());
        account.setState(accountDto.getState());
        account.setCreatedAt(accountDto.getCreatedAt());

        AccountDto accountDto1 = new AccountDto();
        accountDto1.setBalance(new BigDecimal(200));
        accountDto1.setNumber("11564007845335");
        accountDto1.setHolderId("635a098080b99b212dbf0b42");
        accountDto1.setProductId("93596a9a3e79080adc09ae4a");
        accountDto1.setCoin("PEN");
        accountDto1.setState(StateAccount.ACTIVE);
        accountDto1.setCreatedAt(new Date());

        Account account1 = new Account();
        account1.setBalance(accountDto1.getBalance());
        account1.setNumber(accountDto1.getNumber());
        account1.setHolderId(accountDto1.getHolderId());
        account1.setProductId(accountDto1.getProductId());
        account1.setCoin(accountDto1.getCoin());
        account1.setState(accountDto1.getState());
        account1.setCreatedAt(accountDto1.getCreatedAt());

        when(accountRepository.findByHolderId("635a098080b99b212dbf0b42")).thenReturn(Flux.just(account, account1));

        var responseFlux= webTestClient.get().uri("/account/customer/635a098080b99b212dbf0b42")
                .exchange()
                .expectStatus().isOk()
                .returnResult(AccountDto.class)
                .getResponseBody();


        StepVerifier.create(responseFlux)
                .expectSubscription()
                .expectNext(accountDto)
                .expectNext(accountDto1)
                .verifyComplete();

    }

    @Test
    @DisplayName("You should get a successful response when deleting an account")
    void delete(){
        Account account = new Account();
        account.setId("678a098080b99b212dbf0b42");
        account.setBalance(new BigDecimal(100));
        account.setNumber("11564889845335");
        account.setHolderId("635a098080b99b212dbf0b42");
        account.setProductId("63596a9a3e79080adc09ae4a");
        account.setCoin("PEN");
        account.setState(StateAccount.ACTIVE);
        account.setCreatedAt(new Date());
        account.setTypeAccount(TypeAccount.SAVINGS_ACCOUNT);

        when(accountRepository.findById("678a098080b99b212dbf0b42")).thenReturn(Mono.just(account));
        when(savingsAccountService.delete("678a098080b99b212dbf0b42")).thenReturn(Mono.empty());

        this.webTestClient
                .delete()
                .uri("/account/678a098080b99b212dbf0b42")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(AccountDto.class);
    }

    @TestConfiguration
    static class AccountControllerTestConfig {
        @Bean
        public AccountService<AccountDto> accountService() {
            return new AccountServiceImpl();
        }

        @Bean
        public MapperAccount mapperAccount() {
            return new MapperAccount();
        }
    }
}
