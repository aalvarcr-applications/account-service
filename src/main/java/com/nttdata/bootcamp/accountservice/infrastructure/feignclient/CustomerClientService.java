package com.nttdata.bootcamp.accountservice.infrastructure.feignclient;

import com.nttdata.bootcamp.accountservice.application.exception.ServiceUnavailableException;
import com.nttdata.bootcamp.accountservice.model.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 */
@Component
public class CustomerClientService {
    @Autowired
    CustomerClient customerClient;
    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    public Mono<CustomerDto> getCustomer(String id) {
        return reactiveCircuitBreakerFactory.create("${circuitbreaker.instances.name}")
                .run(customerClient.getCustomer(id),
                  throwable -> Mono.error(new ServiceUnavailableException(
                    "The service is not available, try in a few minutes please")));
    }
}
