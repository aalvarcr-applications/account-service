package com.nttdata.bootcamp.accountservice.application.controller;

import com.nttdata.bootcamp.accountservice.application.service.AccountService;
import com.nttdata.bootcamp.accountservice.model.dto.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;


/**
 *
 * @since 2022
 */
@RefreshScope
@RestController
public class AccountController {
  @Autowired
  AccountService<AccountDto> accountService;

  @PostMapping("account")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<AccountDto> create(@Valid @RequestBody AccountDto accountDto) {
    return accountService.create(accountDto);
  }

  @GetMapping("account/{number}")
  public Mono<ResponseEntity<AccountDto>> findByNumber(@PathVariable String number) {
    return accountService.findByNumber(number)
                .map(a -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(a))
                .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("account/customer/{holderId}")
  public Flux<AccountDto> findByHolderId(@PathVariable("holderId") String holderId) {
    return accountService.findByHolderId(holderId);
  }

  @DeleteMapping("account/{id}")
  public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
    return accountService.delete(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
  }

  /**
   * javadoc.
   * Resume products by customer ID
   * @since 2022
   */
  @GetMapping(value = "account/product", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<AccountDto> findByCreateDateBetweenAndIdProduct(
    @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date startDate,
    @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date endDate,
    @RequestParam String productId) {

    return accountService.findByCreatedDateBetweenAndProductId(startDate, endDate, productId);

  }
}
