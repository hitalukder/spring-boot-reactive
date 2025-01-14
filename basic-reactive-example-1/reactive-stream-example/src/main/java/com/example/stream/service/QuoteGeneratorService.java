package com.example.stream.service;

import java.time.Duration;

import com.example.stream.model.Quote;

import reactor.core.publisher.Flux;

public interface QuoteGeneratorService {
    Flux<Quote> fetchQuoteStream(Duration period);
}
