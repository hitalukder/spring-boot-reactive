package com.mongo.react.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mongo.react.domain.QuoteHistory;

public interface QuoteHistoryRepository extends ReactiveMongoRepository<QuoteHistory, String> {

}
