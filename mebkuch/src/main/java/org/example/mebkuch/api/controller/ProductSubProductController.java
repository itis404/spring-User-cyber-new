package org.example.mebkuch.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.application.usecase.product.ProductSubProductUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/similar")
@RequiredArgsConstructor
public class ProductSubProductController {

    private final ProductSubProductUseCase useCase;

    @GetMapping("/{productId}")
    public List<ProductDto> get(@PathVariable Long productId) {
        return useCase.getSimilar(productId);
    }
}