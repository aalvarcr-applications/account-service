package com.nttdata.bootcamp.accountservice.infrastructure.feignclient;

import com.nttdata.bootcamp.accountservice.application.exception.ServiceUnavailableException;
import com.nttdata.bootcamp.accountservice.model.dto.CreditCardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 *
 * @since 2022
 */
@Component
public class CreditClientService {
    @Autowired
    CreditClient creditClient;

    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    public Flux<CreditCardDto> getCreditCardCustomer(String id){
        return reactiveCircuitBreakerFactory.create("${circuitbreaker.instances.name}")
                .run(creditClient.getCreditCardCustomer(id),
                  throwable -> Flux.error(new ServiceUnavailableException(
                    "The service is not available, try in a few minutes please")));

    }
}
