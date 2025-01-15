package com.rabbitmq.reactive.service;

import java.time.Duration;

import com.rabbitmq.reactive.model.Quote;

import reactor.core.publisher.Flux;

public interface QuoteGeneratorService {

    Flux<Quote> fetchQuoteStream(Duration period);
}