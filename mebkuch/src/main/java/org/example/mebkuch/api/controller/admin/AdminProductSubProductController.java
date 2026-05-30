package org.example.mebkuch.api.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.application.usecase.product.ProductSubProductUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product/similar")
@RequiredArgsConstructor
public class AdminProductSubProductController {

    private final ProductSubProductUseCase useCase;

    @PostMapping("/{productId}/{subProductId}")
    public void add(@PathVariable Long productId,
                    @PathVariable Long subProductId) {
        useCase.add(productId, subProductId);
    }

    @DeleteMapping("/{productId}/{subProductId}")
    public void remove(@PathVariable Long productId,
                       @PathVariable Long subProductId) {
        useCase.remove(productId, subProductId);
    }

    @GetMapping("/{productId}")
    public List<ProductDto> get(@PathVariable Long productId) {
        return useCase.getSimilar(productId);
    }
}
