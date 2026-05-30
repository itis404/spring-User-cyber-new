package org.example.mebkuch.application.usecase.reference_books.status;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductStatusDto;
import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.example.mebkuch.domain.service.reference_books.ProductStatusService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductStatusCommandUseCase {

    private final ProductStatusService productStatusService;


    public ProductStatusDto create(ProductStatusDto dto) {

        ProductStatusModel model = ProductStatusModel.builder()
                .name(dto.getName())
                .build();

        ProductStatusModel saved = productStatusService.create(model);

        return mapToDto(saved);
    }


    public ProductStatusDto update(Long id, String newName) {

        ProductStatusModel updated = productStatusService.update(id, newName);

        return mapToDto(updated);
    }


    public void delete(Long id) {
        productStatusService.delete(id);
    }


    private ProductStatusDto mapToDto(ProductStatusModel model) {
        return ProductStatusDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }
}