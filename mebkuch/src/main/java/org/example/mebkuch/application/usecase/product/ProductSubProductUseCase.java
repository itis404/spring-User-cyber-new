package org.example.mebkuch.application.usecase.product;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.api.mapper.ProductDtoModelMapper;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.service.product.ProductSubProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSubProductUseCase {

    private final ProductSubProductService service;

    public void add(Long productId, Long subProductId) {
        service.addSimilarProduct(productId, subProductId);
    }

    public void remove(Long productId, Long subProductId) {
        service.removeSimilarProduct(productId, subProductId);
    }

    public List<ProductDto> getSimilar(Long productId) {
        return service.getSimilarProducts(productId).stream().map(ProductDtoModelMapper::toDto).toList();
    }
}
