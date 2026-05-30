package org.example.mebkuch.application.usecase.reference_books.style;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductStyleDto;
import org.example.mebkuch.domain.models.reference_book.ProductStyleModel;
import org.example.mebkuch.domain.service.reference_books.ProductStyleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductStyleCommandUseCase {

    private final ProductStyleService productStyleService;

    public ProductStyleDto create(ProductStyleDto dto) {

        ProductStyleModel model = ProductStyleModel.builder()
                .name(dto.getName())
                .build();

        ProductStyleModel saved = productStyleService.create(model);

        return mapToDto(saved);
    }


    public ProductStyleDto update(Long id, String newName) {

        ProductStyleModel updated = productStyleService.update(id, newName);

        return mapToDto(updated);
    }


    public void delete(Long id) {
        productStyleService.delete(id);
    }


    private ProductStyleDto mapToDto(ProductStyleModel model) {
        return ProductStyleDto.builder()
                .id(model.getId())
                .name(model.getName())
                .imageUrl(model.getImageUrl())
                .build();
    }
}