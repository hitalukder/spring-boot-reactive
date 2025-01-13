package com.reactive.brewery.services;
import java.util.UUID;
import com.reactive.brewery.web.model.CustomerDto;


public interface CustomerService {
    CustomerDto getCustomerById(UUID customerId);

    CustomerDto saveNewCustomer(CustomerDto customerDto);

    void updateCustomer(UUID customerId, CustomerDto customerDto);

    void deleteById(UUID customerId);
}
