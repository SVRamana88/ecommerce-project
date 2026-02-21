package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.ProductDto;
import com.svr.ecommerce.entities.Product;
import com.svr.ecommerce.mappers.ProductMapper;
import com.svr.ecommerce.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    final private ProductRepository productRepository;
    final private ProductMapper productMapper;

    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestHeader(name = "x-api-token", required = false) String apiToken,
            @RequestParam(name = "categoryId", required = false) Byte categoryId)
    {

        System.out.println(apiToken);

        List<Product> products = (categoryId != null)
                ? productRepository.findProductByCategoryId(categoryId)
                : productRepository.findAllWithCategory();

        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if(product == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productMapper.toDto(product));
    }

}
