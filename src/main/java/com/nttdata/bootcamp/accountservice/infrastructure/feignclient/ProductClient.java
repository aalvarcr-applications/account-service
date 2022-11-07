package com.nttdata.bootcamp.accountservice.infrastructure.feignclient;

import com.nttdata.bootcamp.accountservice.model.dto.ProductDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 *
 * @since 2022
 */
@ReactiveFeignClient(name = "${feign.service.product.name}")
public interface ProductClient {
    @RequestMapping(method = RequestMethod.GET, value = "/product/get/{id}")
    Mono<ProductDto> getProductAccount(@PathVariable("id") String id);
}
