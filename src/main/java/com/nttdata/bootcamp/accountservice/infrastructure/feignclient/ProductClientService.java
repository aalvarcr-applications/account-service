package com.nttdata.bootcamp.accountservice.infrastructure.feignclient;

import com.nttdata.bootcamp.accountservice.application.exception.ServiceUnavailableException;
import com.nttdata.bootcamp.accountservice.model.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 */
@Component
public class ProductClientService {
    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @Autowired
    ProductClient productClient;

    public Mono<ProductDto> getProductAccount(String id) {
        return reactiveCircuitBreakerFactory.create("${circuitbreaker.instances.name}")
                .run(productClient.getProductAccount(id),
                  throwable -> Mono.error(new ServiceUnavailableException(
                    "The service is not available, try in a few minutes please")));
    }
}
