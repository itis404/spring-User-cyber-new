package org.example.mebkuch.application.usecase.reference_books.status;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductStatusDto;
import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.example.mebkuch.domain.service.reference_books.ProductStatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductStatusQueryUseCase {

    private final ProductStatusService productStatusService;


    public ProductStatusDto getById(Long id) {
        return mapToDto(productStatusService.getById(id));
    }


    public Page<ProductStatusDto> getAll(Pageable pageable) {
        return productStatusService.getAll(pageable)
                .map(this::mapToDto);
    }

    public List<ProductStatusDto> getByNameContains(String nameContains) {
        return productStatusService.getByNameContains(nameContains)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ProductStatusDto mapToDto(ProductStatusModel model) {
        return ProductStatusDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }
}