package org.example.mebkuch.api.controller.admin.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.api.dto.reference_books.ProductSectionDto;
import org.example.mebkuch.api.validation.ProductSectionValidatorDto;
import org.example.mebkuch.application.usecase.reference_books.section.ProductSectionCommandUseCase;
import org.example.mebkuch.application.usecase.reference_books.section.ProductSectionQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/product-section")
@RequiredArgsConstructor
public class AdminProductSectionController {
    private final ProductSectionQueryUseCase productSectionQueryUseCase;
    private final ProductSectionCommandUseCase productSectionCommandUseCase;

    @PostMapping
    public ResponseEntity<ProductSectionDto> create(@RequestBody ProductSectionDto dto) {
        ProductSectionValidatorDto.validate(dto);
        ProductSectionDto productSectionDto = productSectionCommandUseCase.createProductSection(dto);
        return ResponseEntity.ok().body(productSectionDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductSectionDto> update(@PathVariable Long id,
                                                    @RequestParam String name) {
        ProductSectionValidatorDto.validateName(name);
        ProductSectionDto productSectionDto = productSectionCommandUseCase.updateProductSection(id, name);

        return ResponseEntity.ok().body(productSectionDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productSectionCommandUseCase.deleteProductSection(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<ProductSectionDto> getAll(@ParameterObject
                                          @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                          Pageable pageable){
        return productSectionQueryUseCase.getAll(pageable);
    }

    @GetMapping("/{sectionId}")
    public Page<ProductDto> getProductsBySection(@PathVariable("sectionId") Long sectionId,
                                                 @ParameterObject
                                                 @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                 Pageable pageable){
        return productSectionQueryUseCase.getProductsBySection(sectionId, pageable);
    }

    @PostMapping("/product-to/{sectionId}")
    public void addProductToSection(Long sectionId, Long productId){
        productSectionCommandUseCase.addProductToSection(sectionId, productId);
    }

    @DeleteMapping("{sectionId}/{productId}")
    public void removeProductFromSection(Long sectionId, Long productId) {
        productSectionCommandUseCase.removeProductFromSection(sectionId, productId);
    }

}
