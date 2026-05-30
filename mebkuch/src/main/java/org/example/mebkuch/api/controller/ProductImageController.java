package org.example.mebkuch.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.image.ProductImageDto;
import org.example.mebkuch.application.usecase.image.ImageUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-image")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ImageUseCase imageUseCase;

    @GetMapping("/{id}")
    public ProductImageDto getById(@PathVariable Long id) {
        return imageUseCase.getById(id);
    }

    @GetMapping("/product/{productId}")
    public List<ProductImageDto> getByProductId(@PathVariable Long productId) {
        return imageUseCase.getByProductId(productId);
    }

}