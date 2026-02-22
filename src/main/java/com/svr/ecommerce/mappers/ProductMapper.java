package com.svr.ecommerce.mappers;

import com.svr.ecommerce.dtos.ProductDto;
import com.svr.ecommerce.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);
    Product toEntity(ProductDto request);
    @Mapping(target = "id", ignore = true)
    void update(ProductDto request, @MappingTarget Product product);
}
