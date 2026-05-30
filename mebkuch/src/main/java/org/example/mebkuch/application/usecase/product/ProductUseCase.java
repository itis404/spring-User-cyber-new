package org.example.mebkuch.application.usecase.product;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.product.CreateProductDto;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.api.dto.product.ProductExtraFieldsDto;
import org.example.mebkuch.api.mapper.ProductDtoModelMapper;
import org.example.mebkuch.api.mapper.ProductExtraFieldsDtoModelMapper;
import org.example.mebkuch.domain.models.filter.ProductFilter;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.service.product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductService productService;

    public Page<ProductDto> getProducts(ProductFilter productFilter, Pageable pageable) {
        Page<ProductModel> productModel = productService.getProducts(productFilter, pageable);
        return productModel.map(ProductDtoModelMapper::toDto);
    }

    public void addExtraFields(Long productId, ProductExtraFieldsDto productExtraFieldsDto){
        productService.addExtraFields(productId, ProductExtraFieldsDtoModelMapper.toModel(productExtraFieldsDto));
    }

    public Page<ProductDto> getProductWithMaxDiscount(Pageable pageable) {
        Page<ProductModel> productModel = productService.getProductWithMaxDiscount(pageable);
        return productModel.map(ProductDtoModelMapper::toDto);
    }

    public ProductDto create(CreateProductDto dto) {

        ProductModel saved = productService.create(
                ProductDtoModelMapper.toModelForCreate(dto)
        );

        return ProductDtoModelMapper.toDto(saved);
    }


    public ProductDto getById(Long id) {

        ProductModel model = productService.getById(id);

        return ProductDtoModelMapper.toDto(model);
    }

    public void delete(Long id) {
        productService.delete(id);
    }
}
