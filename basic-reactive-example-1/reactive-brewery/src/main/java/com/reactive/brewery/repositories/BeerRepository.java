package com.reactive.brewery.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.reactive.brewery.domain.Beer;

import reactor.core.publisher.Mono;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {
    // Page<Beer> findAllByBeerName(String beerName, Pageable pageable);

    // Page<Beer> findAllByBeerStyle(BeerStyleEnum beerStyle, Pageable pageable);

    // Page<Beer> findAllByBeerNameAndBeerStyle(String beerName, BeerStyleEnum
    // beerStyle, Pageable pageable);

    Mono<Beer> findByUpc(String upc);

//   these DO NOT WORK, but MAY in future
//    Flux<Page<Beer>> findAllByBeerName(String beerName, Pageable pageable);
//
//    Flux<Page<Beer>> findAllByBeerStyle(BeerStyleEnum beerStyle, Pageable pageable);
//
//    Flux<Page<Beer>> findAllByBeerNameAndBeerStyle(String beerName, BeerStyleEnum beerStyle, Pageable pageable);
//
//    Flux<Page<Beer>> findBeerBy(Pageable pageable);
}
