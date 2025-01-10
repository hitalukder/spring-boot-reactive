package com.reactive.example.client;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import com.reactive.example.dto.BeerDto;
import com.reactive.example.dto.BeerPagedList;
import reactor.core.publisher.Mono;

public interface BeerClient {
    Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand);

    Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName,
            String beerStyle, Boolean showInventoryOnhand);

    Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto);

    Mono<ResponseEntity<Void>> updateBeer(UUID beerId, BeerDto beerDto);

    Mono<ResponseEntity<Void>> deleteBeerById(UUID id);

    Mono<BeerDto> getBeerByUPC(String upc);
}
