package org.example.mebkuch.api.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.image.ProductImageDto;
import org.example.mebkuch.api.validation.ProductImageValidatorDto;
import org.example.mebkuch.application.usecase.image.ImageUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product-image")
@RequiredArgsConstructor
@Slf4j
public class AdminProductImageController {

    private final ImageUseCase imageUseCase;

    @PostMapping
    public ProductImageDto create(@RequestBody ProductImageDto dto) {
        ProductImageValidatorDto.validate(dto);
        return imageUseCase.create(dto);
    }

    @PutMapping("/{id}")
    public ProductImageDto update(@PathVariable Long id,
                                  @RequestParam String imagePath,
                                  @RequestParam(required = false) Boolean isMain,
                                  @RequestParam(required = false) Integer sortOrder) {
        ProductImageValidatorDto.validatePath(imagePath);
        return imageUseCase.update(id, imagePath, isMain, sortOrder);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return imageUseCase.delete(id);
    }

    @GetMapping("/{id}")
    public ProductImageDto getById(@PathVariable Long id) {
        return imageUseCase.getById(id);
    }

    @GetMapping("/product/{productId}")
    public List<ProductImageDto> getByProductId(@PathVariable Long productId) {
        return imageUseCase.getByProductId(productId);
    }

    @PostMapping("/product/{productId}/set-main/{imageId}")
    public ProductImageDto setMainImage(@PathVariable Long productId, @PathVariable Long imageId) {
        return imageUseCase.setMainImage(productId, imageId);
    }
}