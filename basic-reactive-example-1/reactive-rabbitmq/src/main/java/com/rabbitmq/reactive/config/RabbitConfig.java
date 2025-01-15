package com.rabbitmq.reactive.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Connection;

import reactor.core.publisher.Mono;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
public class RabbitConfig {
    public static final String QUEUE = "quotes";

    

    @Bean
    public Mono<Connection> connectionMono(CachingConnectionFactory connectionFactory){
        return Mono.fromCallable(() -> connectionFactory.getRabbitConnectionFactory().newConnection());
    }

    

    @Bean
    public Sender sender(Mono<Connection> mono) {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(mono));
    }

    @Bean
    public Receiver receiver(Mono<Connection> mono) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(mono));
    }
}
