package org.example.mebkuch.application.usecase.reference_books.style;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductStyleDto;
import org.example.mebkuch.domain.models.reference_book.ProductStyleModel;
import org.example.mebkuch.domain.service.reference_books.ProductStyleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStyleQueryUseCase {

    private final ProductStyleService productStyleService;

    public ProductStyleDto getById(Long id) {
        return mapToDto(productStyleService.getById(id));
    }

    public Page<ProductStyleDto> getAll(Pageable pageable) {
        return productStyleService.getAll(pageable)
                .map(this::mapToDto);
    }

    public List<ProductStyleDto> getByNameContains(String nameContains) {
        return productStyleService.getByNameContains(nameContains)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private ProductStyleDto mapToDto(ProductStyleModel model) {
        return ProductStyleDto.builder()
                .id(model.getId())
                .name(model.getName())
                .imageUrl(model.getImageUrl())
                .build();
    }
}