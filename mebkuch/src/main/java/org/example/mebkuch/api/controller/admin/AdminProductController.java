package org.example.mebkuch.api.controller.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.product.CreateProductDto;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.api.dto.product.ProductExtraFieldsDto;
import org.example.mebkuch.api.validation.ProductValidatorDto;
import org.example.mebkuch.application.usecase.product.ProductUseCase;
import org.example.mebkuch.domain.models.filter.ProductFilter;
import org.example.mebkuch.domain.service.product.ProductAttributeService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

    private final ProductUseCase productUseCase;
    private final ProductAttributeService productAttributeService;

    @PostMapping
    public ProductDto create(@RequestBody CreateProductDto dto) {
        ProductValidatorDto.validateForeCreate(dto);
        return productUseCase.create(dto);
    }

    @PatchMapping("{product-id}/add-extra-fields")
    public void addExtraFields(@PathVariable("product-id") Long productId, @RequestBody ProductExtraFieldsDto productExtraFieldsDto){
        productUseCase.addExtraFields(productId, productExtraFieldsDto);
    }


    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productUseCase.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productUseCase.delete(id);
    }

    @PostMapping("/hard-search")
    public Page<ProductDto> getProducts(
            @RequestBody(required = false) ProductFilter filter,
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("FILTER: " + filter.toString());
        log.info("PAGEABLE: {}", pageable);
        return productUseCase.getProducts(filter, pageable);
    }


    @PostMapping("/{productId}/attributes")
    public void addAttributes(@PathVariable Long productId,
                              @RequestBody List<Long> attributeValueIds) {

        productAttributeService.addAttributes(productId, attributeValueIds);
    }


    @PutMapping("/{productId}/attributes")
    public void replaceAttributes(@PathVariable Long productId,
                                  @RequestBody List<Long> attributeValueIds) {

        productAttributeService.replaceAttributes(productId, attributeValueIds);
    }


    @DeleteMapping("/{productId}/attributes/{attributeValueId}")
    public void removeAttribute(@PathVariable Long productId,
                                @PathVariable Long attributeValueId) {

        productAttributeService.removeAttribute(productId, attributeValueId);
    }


    @PostMapping("/{productId}/attributes/rebuild-index")
    public void rebuildIndex(@PathVariable Long productId) {
        productAttributeService.rebuildIndex(productId);
    }

}