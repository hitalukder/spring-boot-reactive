package com.rabbitmq.reactive.service;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.reactive.config.RabbitConfig;
import com.rabbitmq.reactive.model.Quote;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Component
@RequiredArgsConstructor
public class QuoteMessageSender {
    private final ObjectMapper objectMapper;
    private final Sender sender;

    @SneakyThrows
    public Mono<Void> sendQuoteMessage(Quote quote){
        byte[] jsonBytes = objectMapper.writeValueAsBytes(quote);

        return sender.send(Mono.just(new OutboundMessage("", RabbitConfig.QUEUE, jsonBytes)));
    }
}
