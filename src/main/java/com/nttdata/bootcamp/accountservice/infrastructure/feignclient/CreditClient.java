package com.nttdata.bootcamp.accountservice.infrastructure.feignclient;

import com.nttdata.bootcamp.accountservice.model.dto.CreditCardDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

/**
 *
 * @since 2022
 */
@ReactiveFeignClient(name = "${feign.service.credit.name}")
public interface CreditClient {
    @RequestMapping(method = RequestMethod.GET, value = "/credit/card/customer/{id}")
    Flux<CreditCardDto> getCreditCardCustomer(@PathVariable("id") String id);
}
