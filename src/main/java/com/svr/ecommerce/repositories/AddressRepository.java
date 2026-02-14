package com.svr.ecommerce.repositories;

import com.svr.ecommerce.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}