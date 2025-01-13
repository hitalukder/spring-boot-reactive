package com.reactive.brewery.services;

import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import static org.springframework.data.relational.core.query.Criteria.where;
import org.springframework.data.relational.core.query.Query;
import static org.springframework.data.relational.core.query.Query.empty;
import static org.springframework.data.relational.core.query.Query.query;
import org.springframework.stereotype.Service;

import com.reactive.brewery.domain.Beer;
import com.reactive.brewery.repositories.BeerRepository;
import com.reactive.brewery.web.mappers.BeerMapper;
import com.reactive.brewery.web.model.BeerDto;
import com.reactive.brewery.web.model.BeerPagedList;
import com.reactive.brewery.web.model.BeerStyleEnum;

import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final R2dbcEntityTemplate template;

    @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false ")
    @Override
    public Mono<BeerPagedList> listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest,
            Boolean showInventoryOnHand) {

        Query query = null;

        if (!StringUtil.isNullOrEmpty(beerName) && beerStyle != null) {
            // search both
            query = query(where("beerName").is(beerName).and("beerStyle").is(beerStyle.toString()));
        } else if (!StringUtil.isNullOrEmpty(beerName) && beerStyle == null) {
            // search beer_service name
            query = query(where("beerName").is(beerName));
        } else if (beerStyle != null && StringUtil.isNullOrEmpty(beerName)) {
            // search beer_service style
            query = query(where("beerStyle").is(beerStyle));
        } else {
            query = empty();
        }

        return template.select(Beer.class)
                .matching(query.with(pageRequest))
                .all()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList())
                .map(beers -> {
                    return new BeerPagedList(beers, PageRequest.of(
                            pageRequest.getPageNumber(),
                            pageRequest.getPageSize()),
                            beers.size());
                });
    }

    @Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false ")
    @Override
    public Mono<BeerDto> getById(Integer beerId, Boolean showInventoryOnHand) {

        if (showInventoryOnHand) {
            return beerRepository.findById(beerId).map(beerMapper::beerToBeerDtoWithInventory);
        } else {
            return beerRepository.findById(beerId).map(beerMapper::beerToBeerDto);
        }
    }

    @Override
    public Mono<BeerDto> saveNewBeer(BeerDto beerDto) {
        return beerRepository.save(beerMapper.beerDtoToBeer(beerDto)).map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDto> updateBeer(Integer beerId, BeerDto beerDto) {

        return beerRepository.findById(beerId).defaultIfEmpty(Beer.builder().build()).map(beer -> {
            beer.setBeerName(beerDto.getBeerName());
            beer.setBeerStyle(BeerStyleEnum.valueOf(beerDto.getBeerStyle()));
            beer.setPrice(beerDto.getPrice());
            beer.setUpc(beerDto.getUpc());
            return beer;
        }).flatMap(updatedBeer -> {
            if (updatedBeer.getId() != null) {
                return beerRepository.save(updatedBeer);
            }
            return Mono.just(updatedBeer);

        }).map(beerMapper::beerToBeerDto);

    }

    @Cacheable(cacheNames = "beerUpcCache")
    @Override
    public Mono<BeerDto> getByUpc(String upc) {
        return beerRepository.findByUpc(upc).map(beerMapper::beerToBeerDto);
    }

    @Override
    public void deleteBeerById(Integer beerId) {
        beerRepository.deleteById(beerId);
    }
}
