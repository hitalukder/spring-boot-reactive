package com.reactive.brewery.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.reactive.brewery.domain.Beer;
import com.reactive.brewery.web.model.BeerDto;

@Mapper(uses = { DateMapper.class })
public interface BeerMapper {

    @Mapping(target = "quantityOnHand", ignore = true)
    BeerDto beerToBeerDto(Beer beer);

    BeerDto beerToBeerDtoWithInventory(Beer beer);

    Beer beerDtoToBeer(BeerDto dto);
}
