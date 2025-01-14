package com.mongo.react.service;

import java.time.Duration;

import com.mongo.react.model.Quote;

import reactor.core.publisher.Flux;

public interface QuoteGeneratorService {
    Flux<Quote> fetchQuoteStream(Duration period);
}