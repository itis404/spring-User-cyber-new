package org.example.mebkuch.application.usecase.image;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.image.ProductImageDto;
import org.example.mebkuch.api.mapper.ProductImageDtoModelMapper;
import org.example.mebkuch.domain.models.image.ProductImageModel;
import org.example.mebkuch.domain.service.image.ImageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageUseCase {

    private final ImageService imageService;

    public ProductImageDto create(ProductImageDto dto) {
        ProductImageModel model = ProductImageDtoModelMapper.toModel(dto);
        ProductImageModel saved = imageService.create(model);
        return ProductImageDtoModelMapper.toDto(saved);
    }

    public ProductImageDto update(Long id, String imagePath, Boolean isMain, Integer sortOrder) {
        ProductImageModel updated = imageService.update(id, imagePath, isMain, sortOrder);
        return ProductImageDtoModelMapper.toDto(updated);
    }

    public boolean delete(Long id) {
        return imageService.delete(id);
    }


    public ProductImageDto getById(Long id) {
        ProductImageModel model = imageService.findById(id);
        return ProductImageDtoModelMapper.toDto(model);
    }

    public List<ProductImageDto> getByProductId(Long productId) {
        List<ProductImageModel> models = imageService.findByProductId(productId);
        return models.stream()
                .map(ProductImageDtoModelMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductImageDto setMainImage(Long productId, Long imageId) {
        ProductImageModel updated = imageService.setMainImage(productId, imageId);
        return ProductImageDtoModelMapper.toDto(updated);
    }
}