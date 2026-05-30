package org.example.mebkuch.application.usecase.reference_books.section;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.api.dto.reference_books.ProductSectionDto;
import org.example.mebkuch.api.mapper.ProductDtoModelMapper;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.models.reference_book.ProductSectionModel;
import org.example.mebkuch.domain.service.reference_books.ProductSectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSectionQueryUseCase {

    private final ProductSectionService productSectionService;

    public List<ProductSectionDto> getByNameContains(String nameContains) {
        return productSectionService.getByNameContains(nameContains)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public Page<ProductSectionDto> getAll(Pageable pageable) {
        return productSectionService.getAll(pageable)
                .map(this::mapToDto);
    }

    public Page<ProductDto> getProductsBySection(Long sectionId, Pageable pageable) {
        return productSectionService.getProductsBySection(sectionId, pageable).map(ProductDtoModelMapper::toDto);
    }

    private ProductSectionDto mapToDto(ProductSectionModel model) {
        return ProductSectionDto.builder()
                .id(model.getId())
                .name(model.getName())
                .imageUrl(model.getImageUrl())
                .build();
    }
}