package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.ProductDto;
import com.svr.ecommerce.entities.Product;
import com.svr.ecommerce.mappers.ProductMapper;
import com.svr.ecommerce.repositories.CategoryRepository;
import com.svr.ecommerce.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;
@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    final private ProductRepository productRepository;
    final private CategoryRepository categoryRepository;
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


    @PostMapping()
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto request,
            UriComponentsBuilder uriBuilder
    ) {
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if(category == null) return ResponseEntity.badRequest().build();

        var product = productMapper.toEntity(request);
        product.setCategory(category);
        productRepository.save(product);

        System.out.println(product);

        var productDto = productMapper.toDto(product);
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(productDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto request
    ) {
        var product = productRepository.findById(id).orElse(null);
        if(product == null) return ResponseEntity.notFound().build();

        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if(category == null) return ResponseEntity.badRequest().build();

        product.setCategory(category);
        productMapper.update(request, product);
        System.out.println(product);
        productRepository.save(product);
        request.setId(id);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if(product == null) return ResponseEntity.notFound().build();
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}
