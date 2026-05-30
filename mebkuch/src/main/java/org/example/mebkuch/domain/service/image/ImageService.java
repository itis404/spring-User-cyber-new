package org.example.mebkuch.domain.service.image;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.models.image.ProductImageModel;
import org.example.mebkuch.domain.repository.IProductImageRepository;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final IProductImageRepository productImageRepository;
    private final IProductRepository productRepository;

    @Transactional
    public ProductImageModel create(ProductImageModel productImageModel) {
        validateBeforeCreate(productImageModel);
        if (Boolean.TRUE.equals(productImageModel.getIsMain())) {
            ensureMainImageUniqueness(productImageModel.getProductId(), null);
        }
        return productImageRepository.save(productImageModel);
    }


    private void validateBeforeCreate(ProductImageModel model) {
        if (model == null) {
            throw new ProductException("Модель изображения не может быть null");
        }
        if (model.getImagePath() == null || model.getImagePath().isBlank()) {
            throw new ProductException("Путь к изображению обязателен");
        }
        if (model.getSortOrder() != null && model.getSortOrder() < 0) {
            throw new ProductException("sortOrder не может быть отрицательным");
        }
    }

    @Transactional
    public ProductImageModel update(Long id, String imagePath, Boolean isMain, Integer sortOrder) {
        if (imagePath == null || imagePath.isBlank()) {
            throw new ProductException("Путь к изображению не может быть пустым");
        }
        if (sortOrder != null && sortOrder < 0) {
            throw new ProductException("sortOrder не может быть отрицательным");
        }
        if (!productImageRepository.existsById(id)) {
            throw new ProductException("Изображение с id " + id + " не найдено");
        }

        if (Boolean.TRUE.equals(isMain)) {
            ProductImageModel existing = productImageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Изображение не найдено"));
            ensureMainImageUniqueness(existing.getProductId(), id);
        }
        return productImageRepository.update(id, imagePath, isMain, sortOrder);
    }

    public boolean delete(Long id) {
        if (!productImageRepository.existsById(id)) {
            return false;
        }
        return productImageRepository.deleteById(id);
    }

    public ProductImageModel findById(Long id) {
        return productImageRepository.findById(id)
                .orElseThrow(() -> new ProductException("не существует такого изображения"));
    }

    public List<ProductImageModel> findByProductId(Long productId) {

        if (productRepository.existsById(productId)){
            List<ProductImageModel> productImageModels = productImageRepository.findByProductId(productId);

            productImageModels.sort(Comparator.nullsLast(Comparator.comparing(ProductImageModel::getSortOrder)));

            return productImageModels;
        }

        throw new ProductException("не существует такого продукта");
    }

    @Transactional
    public ProductImageModel setMainImage(Long productId, Long imageId) {
        ProductImageModel image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ProductException("Изображение не найдено у этого продукта"));
        if (!image.getProductId().equals(productId)) {
            throw new ProductException("Изображение не принадлежит продукту с id " + productId);
        }

        update(imageId, image.getImagePath(), true, 1);

        return image;
    }


//      Обеспечивает уникальность главного изображения для продукта.
//      Если isMain = true, сбрасывает флаг у всех изображений того же продукта,
//      кроме указанного excludeImageId\

    private void ensureMainImageUniqueness(Long productId, Long excludeImageId) {
        List<ProductImageModel> images = productImageRepository.findByProductId(productId);
        for (ProductImageModel img : images) {
            if (excludeImageId != null && img.getId().equals(excludeImageId)) {
                swapOrderSort(images, excludeImageId);
                continue; // пропускаем текущее изображение при обновлении так как оно у нас главное
            }

            productImageRepository.update(img.getId(), img.getImagePath(), false, img.getSortOrder());
        }
    }

    private void swapOrderSort(List<ProductImageModel> images, Long excludeImageId) {
        if (images == null || images.isEmpty()) return;

        // Найти изображение с минимальным sortOrder (не null)
        ProductImageModel minOrderImage = images.stream()
                .filter(img -> img.getSortOrder() != null)
                .min(Comparator.comparing(productImageModel -> productImageModel.getSortOrder()))
                .orElse(null);

        if (minOrderImage == null) return;

        // Найти изображение по excludeImageId
        ProductImageModel targetImage = images.stream()
                .filter(img -> img.getId().equals(excludeImageId))
                .findFirst()
                .orElse(null);
        if (targetImage == null) return;

        // Обменять sortOrder
        Integer minOrder = minOrderImage.getSortOrder();
        Integer targetOrder = targetImage.getSortOrder();

        targetImage.setSortOrder(minOrder);
        minOrderImage.setSortOrder(targetOrder);
    }
}
