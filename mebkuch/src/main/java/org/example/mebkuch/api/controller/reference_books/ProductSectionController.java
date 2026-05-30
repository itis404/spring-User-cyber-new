package org.example.mebkuch.api.controller.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.api.dto.reference_books.ProductSectionDto;
import org.example.mebkuch.application.usecase.reference_books.section.ProductSectionQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product-section")
@RequiredArgsConstructor
@Slf4j
public class ProductSectionController {
    private final ProductSectionQueryUseCase productSectionQueryUseCase;

    @GetMapping
    public Page<ProductSectionDto> getAll(@ParameterObject
                                              @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                              Pageable pageable) {
        return productSectionQueryUseCase.getAll(pageable);
    }

    @GetMapping("/{sectionId}")
    public Page<ProductDto> getProductsBySection(@PathVariable("sectionId") Long sectionId,
                                                 @ParameterObject
                                                 @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                 Pageable pageable){
        return productSectionQueryUseCase.getProductsBySection(sectionId, pageable);
    }

    @GetMapping("/find/{name-contains}")
    public List<ProductSectionDto> getByNameContains(@PathVariable("name-contains") String nameContains) {
        return productSectionQueryUseCase.getByNameContains(nameContains);
    }
}
