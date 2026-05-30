package org.example.mebkuch.domain.service.product;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.example.mebkuch.domain.repository.product.IProductSubProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSubProductService {

    private final IProductSubProductRepository productSubProductRepository;
    private final IProductRepository productRepository;

    public void addSimilarProduct(Long productId, Long subProductId) {

        if (productId.equals(subProductId)) {
            throw new ProductException("продукта по такому id нет");
        }

        if (!productRepository.existsById(productId) ||
                !productRepository.existsById(subProductId)) {
            throw new ProductException("продукта по такому id нет");
        }

        productSubProductRepository.add(productId, subProductId);
    }

    public void removeSimilarProduct(Long productId, Long subProductId) {

        if (!productRepository.existsById(productId) ||
                !productRepository.existsById(subProductId)) {
            throw new ProductException("продукта по такому id нет");
        }

        productSubProductRepository.remove(productId, subProductId);
    }

    public List<ProductModel> getSimilarProducts(Long productId) {
        List<Long> productIds = productSubProductRepository.findSubProducts(productId);

        List<ProductModel> productModels = new ArrayList<>();

        for (Long productIdCurr: productIds){
            ProductModel productModel = productRepository.findById(productIdCurr).orElseThrow(() -> new ProductException("ошибка при создании продукта"));

            productModels.add(productModel);
        }

        return productModels;
    }
}
