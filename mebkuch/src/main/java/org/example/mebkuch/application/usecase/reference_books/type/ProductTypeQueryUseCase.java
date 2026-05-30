package org.example.mebkuch.application.usecase.reference_books.type;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductTypeDto;
import org.example.mebkuch.domain.models.reference_book.ProductTypeModel;
import org.example.mebkuch.domain.service.reference_books.ProductTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTypeQueryUseCase {

    private final ProductTypeService productTypeService;

    public ProductTypeDto getById(Long id) {
        return mapToDto(productTypeService.getById(id));
    }

    public List<ProductTypeDto> getByNameContains(String nameContains) {
        return productTypeService.getByNameContains(nameContains)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public Page<ProductTypeDto> getAll(Pageable pageable) {
        return productTypeService.getAll(pageable)
                .map(this::mapToDto);
    }

    private ProductTypeDto mapToDto(ProductTypeModel model) {
        return ProductTypeDto.builder()
                .id(model.getId())
                .hasComponents(model.getHasComponents())
                .name(model.getName())
                .build();
    }
}