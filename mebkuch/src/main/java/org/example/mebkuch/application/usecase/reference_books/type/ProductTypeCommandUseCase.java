package org.example.mebkuch.application.usecase.reference_books.type;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductTypeDto;
import org.example.mebkuch.domain.models.reference_book.ProductTypeModel;
import org.example.mebkuch.domain.service.reference_books.ProductTypeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductTypeCommandUseCase {

    private final ProductTypeService productTypeService;

    public ProductTypeDto create(ProductTypeDto dto) {

        ProductTypeModel model = ProductTypeModel.builder()
                .hasComponents(dto.getHasComponents())
                .name(dto.getName())
                .build();

        ProductTypeModel saved = productTypeService.create(model);

        return mapToDto(saved);
    }

    public ProductTypeDto update(Long id, String newName) {

        ProductTypeModel updated = productTypeService.update(id, newName);

        return mapToDto(updated);
    }

    public void delete(Long id) {
        productTypeService.delete(id);
    }

    private ProductTypeDto mapToDto(ProductTypeModel model) {
        return ProductTypeDto.builder()
                .id(model.getId())
                .name(model.getName())
                .hasComponents(model.getHasComponents())
                .build();
    }
}