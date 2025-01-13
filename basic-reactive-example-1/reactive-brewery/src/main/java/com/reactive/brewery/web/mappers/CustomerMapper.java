package com.reactive.brewery.web.mappers;

import org.mapstruct.Mapper;

import com.reactive.brewery.domain.Customer;
import com.reactive.brewery.web.model.CustomerDto;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto dto);

    CustomerDto customerToCustomerDto(Customer customer);
}
