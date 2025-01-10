package com.reactive.example.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.reactive.example.config.WebClientConfig;
import com.reactive.example.dto.BeerDto;
import com.reactive.example.dto.BeerPagedList;
import reactor.core.publisher.Mono;

public class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setup() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void testCreateBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("Dogfishhead 90 Min IPA")
                .beerStyle("IPA")
                .upc("234848549559")
                .price(new BigDecimal("10.99"))
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);

        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    void testDeleteBeerById() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null,
                null);

        BeerPagedList pagedList = beerPagedListMono.block();

        UUID beerId = pagedList.getContent().get(0).getId();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(beerId);
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    void testDeleteBeerByIdNotFound() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());

        assertThrows(WebClientResponseException.class, () -> {
            ResponseEntity<Void> responseEntity = responseEntityMono.block();
            assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
        });
    }

    @Test
    void testDeleteBeerByIdHandleException() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());

        ResponseEntity<Void> responseEntity = responseEntityMono.onErrorResume(throwable -> {
            if (throwable instanceof WebClientResponseException) {
                WebClientResponseException exception = (WebClientResponseException) throwable;
                return Mono.just(ResponseEntity.status(exception.getStatusCode()).build());
            } else {
                throw new RuntimeException(throwable);
            }
        }).block();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetBeerById() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null,
                null);

        BeerPagedList pagedList = beerPagedListMono.block();

        UUID beerId = pagedList.getContent().get(0).getId();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId, false);

        BeerDto beerDto = beerDtoMono.block();

        assertNotNull(beerDto);
        assertEquals(beerId, beerDto.getId());
    }

    @Test
    void testGetBeerByUPC() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null,
                null);

        BeerPagedList pagedList = beerPagedListMono.block();

        String upc = pagedList.getContent().get(0).getUpc();
        Mono<BeerDto> beerDtoMono = beerClient.getBeerByUPC(upc);

        BeerDto beerDto = beerDtoMono.block();

        assertNotNull(beerDto);
        assertEquals(upc, beerDto.getUpc());
    }

    @Test
    void testListBeers() {
        Mono<BeerPagedList> monoBeerPageList = beerClient.listBeers(null, null, null, null, null);
        BeerPagedList beerPageList = monoBeerPageList.block();
        assertNotNull(beerPageList);
        assertTrue(beerPageList.getContent().size() > 0);
    }

    @Test
    void testListBeersSize10() {
        Mono<BeerPagedList> monoBeerPageList = beerClient.listBeers(1, 10, null, null, null);
        BeerPagedList beerPageList = monoBeerPageList.block();
        assertNotNull(beerPageList);
        assertEquals(10, beerPageList.getContent().size());
    }

    @Test
    void testListBeersNoRecord() {
        Mono<BeerPagedList> monoBeerPageList = beerClient.listBeers(10, 20, null, null, null);
        BeerPagedList beerPageList = monoBeerPageList.block();
        assertNotNull(beerPageList);
        assertEquals(0, beerPageList.getContent().size());
    }

    @Test
    void testUpdateBeer() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null,
                null);

        BeerPagedList pagedList = beerPagedListMono.block();

        BeerDto beerDto = pagedList.getContent().get(0);
        BeerDto updatedBeer = BeerDto.builder()
                .beerName("Really Good Beer")
                .beerStyle(beerDto.getBeerStyle())
                .price(beerDto.getPrice())
                .upc(beerDto.getUpc())
                .build();
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(beerDto.getId(), updatedBeer);
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    void functionalTestGetBeerById() throws InterruptedException {
        AtomicReference<String> beerName = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        beerClient.listBeers(null, null, null, null,
                null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(beerId -> beerClient.getBeerById(beerId, false))
                .flatMap(mono -> mono)
                .subscribe(beerDto -> {
                    System.out.println(beerDto.getBeerName());
                    beerName.set(beerDto.getBeerName());
                    assertEquals("Mango Bobs",beerDto.getBeerName());
                    countDownLatch.countDown();
                });

        countDownLatch.await();

        assertEquals("Mango Bobs",beerName.get());
    }
}
