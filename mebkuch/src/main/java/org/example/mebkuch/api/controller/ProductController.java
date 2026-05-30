package org.example.mebkuch.api.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.application.usecase.product.ProductUseCase;
import org.example.mebkuch.domain.models.filter.ProductFilter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;


@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductUseCase productUseCase;

    @PostMapping("/hard-search")
    public Page<ProductDto> getProducts(
            @RequestBody ProductFilter filter,
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("FILTER: " + filter.toString());
        log.info("PAGEABLE: {}", pageable);
        return productUseCase.getProducts(filter, pageable);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productUseCase.getById(id);
    }

    @GetMapping("/max-discount")
    Page<ProductDto> getProductWithMaxDiscount(
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return productUseCase.getProductWithMaxDiscount(pageable);
    }

}