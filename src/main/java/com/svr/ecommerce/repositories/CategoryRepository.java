package com.svr.ecommerce.repositories;

import com.svr.ecommerce.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}