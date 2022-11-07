package com.nttdata.bootcamp.accountservice.infrastructure.producer;

import com.nttdata.bootcamp.accountservice.model.dto.TransactionBootcoinDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Class produces payment order events
 * @since 2022
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderBootcoinProducer {
  private final KafkaTemplate<String, TransactionBootcoinDto> kafkaTemplate;

  @Value(value = "${kafka.topic.transaction.result.name}")
  private String topic;

  public void sendMessage(TransactionBootcoinDto transactionRequestDto) {
    ListenableFuture<SendResult<String, TransactionBootcoinDto>> future =
      kafkaTemplate.send(this.topic, transactionRequestDto);

    future.addCallback(new ListenableFutureCallback<SendResult<String, TransactionBootcoinDto>>() {
      @Override
      public void onSuccess(SendResult<String, TransactionBootcoinDto> result) {
        log.info("Message {} has been sent ", transactionRequestDto);
      }

      @Override
      public void onFailure(Throwable ex) {
        log.error("Something went wrong with the balanceModel {} ", transactionRequestDto);
      }
    });
  }
}
