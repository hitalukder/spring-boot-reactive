package com.reactive.brewery.web.functional;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;

import com.reactive.brewery.services.BeerService;
import com.reactive.brewery.web.controller.NotFoundException;
import com.reactive.brewery.web.model.BeerDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerHandlerV2 {
    private final BeerService beerService;
    private final Validator validator;

    public Mono<ServerResponse> getBeerById(ServerRequest request) {
        Integer beerId = Integer.valueOf(request.pathVariable("beerId"));
        Boolean showInventory = Boolean.valueOf(request.queryParam("showInventory").orElse("false"));

        return beerService.getById(beerId, showInventory)
                .flatMap(beerDto -> {
                    return ServerResponse.ok().bodyValue(beerDto);
                }).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getBeerByUpc(ServerRequest request) {
        String upc = request.pathVariable("upc");

        return beerService.getByUpc(upc)
                .flatMap(beerDto -> {
                    return ServerResponse.ok().bodyValue(beerDto);
                }).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveNewBeer(ServerRequest request) {
        Mono<BeerDto> beerDtoMono = request.bodyToMono(BeerDto.class).doOnNext(this::validate);

        return beerService.saveNewBeerMono(beerDtoMono)
                .flatMap(beerDto -> {
                    return ServerResponse.ok()
                            .header("location", BeerRouterConfig.BEER_V2_URL + "/" + beerDto.getId())
                            .build();
                });
    }

    public Mono<ServerResponse> updateBeer(ServerRequest request) {
        return request.bodyToMono(BeerDto.class).doOnNext(this::validate)
                .flatMap(beerDto -> {
                    return beerService.updateBeer(Integer.valueOf(request.pathVariable("beerId")), beerDto);
                }).flatMap(savedBeerDto -> {
                    if (savedBeerDto.getId() != null) {
                        log.debug("Saved Beer Id: {}", savedBeerDto.getId());
                        return ServerResponse.noContent().build();
                    } else {
                        log.debug("Beer Id {} Not Found", request.pathVariable("beerId"));
                        return ServerResponse.notFound().build();
                    }

                });
    }

    public Mono<ServerResponse> deleteBeer(ServerRequest request) {
        return beerService.reactiveDeleteById(Integer.valueOf(request.pathVariable("beerId")))
                .flatMap(voidMono -> {
                    return ServerResponse.ok().build();
                }).onErrorResume(e -> e instanceof NotFoundException,
                        e -> ServerResponse.notFound().build());
    }

    private void validate(BeerDto beerDto) {
        Errors errors = new BeanPropertyBindingResult(beerDto, "beerDto");
        validator.validate(beerDto, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

}
