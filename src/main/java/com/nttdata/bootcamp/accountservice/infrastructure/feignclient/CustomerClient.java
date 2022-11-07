package com.nttdata.bootcamp.accountservice.infrastructure.feignclient;

import com.nttdata.bootcamp.accountservice.model.dto.CustomerDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 */
@ReactiveFeignClient(name = "${feign.service.clients.name}")
public interface CustomerClient {
    @RequestMapping(method = RequestMethod.GET, value = "/customer/get/{id}")
    Mono<CustomerDto> getCustomer(@PathVariable("id") String id);
}
