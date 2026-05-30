package org.example.mebkuch.application.usecase.reference_books.section;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductSectionDto;
import org.example.mebkuch.domain.models.reference_book.ProductSectionModel;
import org.example.mebkuch.domain.service.reference_books.ProductSectionService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSectionCommandUseCase {

    private final ProductSectionService productSectionService;

    public ProductSectionDto createProductSection(ProductSectionDto dto) {
        ProductSectionModel model = ProductSectionModel.builder()
                .name(dto.getName())
                .build();

        ProductSectionModel saved = productSectionService.create(model);

        return mapToDto(saved);
    }


    public ProductSectionDto updateProductSection(Long id, String name) {
        ProductSectionModel updated = productSectionService.update(id, name);
        return mapToDto(updated);
    }


    public void deleteProductSection(Long id) {
        productSectionService.delete(id);
    }


    public void addProductToSection(Long sectionId, Long productId) {
        productSectionService.addProductToSection(sectionId, productId);
    }

    public void removeProductFromSection(Long sectionId, Long productId) {
        productSectionService.removeProductFromSection(sectionId, productId);
    }


    private ProductSectionDto mapToDto(ProductSectionModel model) {
        return ProductSectionDto.builder()
                .id(model.getId())
                .name(model.getName())
                .imageUrl(model.getImageUrl())
                .build();
    }
}