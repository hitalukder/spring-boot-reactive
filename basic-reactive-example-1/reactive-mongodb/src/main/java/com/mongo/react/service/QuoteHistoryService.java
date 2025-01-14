package com.mongo.react.service;

import com.mongo.react.domain.QuoteHistory;
import com.mongo.react.model.Quote;

import reactor.core.publisher.Mono;

public interface QuoteHistoryService {

    Mono<QuoteHistory> saveQuoteToMongo(Quote quote);
}
