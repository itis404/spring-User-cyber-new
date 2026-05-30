package org.example.mebkuch.domain.service.product;

import lombok.RequiredArgsConstructor;

import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.models.eav.AttributeValueModel;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.repository.eav.IAttributeValueRepository;
import org.example.mebkuch.domain.repository.product.IProductAttributeRepository;
import org.example.mebkuch.domain.repository.eav.IProductFilterIndexRepository;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAttributeService {

    private final IProductAttributeRepository productAttributeRepository;
    private final IProductFilterIndexRepository filterIndexRepository;
    private final IAttributeValueRepository attributeValueRepository;
    private final IProductRepository productRepository;


    @Transactional
    public void addAttributes(Long productId, List<Long> attributeValueIds) {

        ProductModel product = getProductOrThrow(productId);

        for (Long valueId : attributeValueIds) {

            AttributeValueModel value = getValueOrThrow(valueId);

            // добавляем связь
            productAttributeRepository.addAttributeToProduct(productId, valueId);

            // обновляем индекс
            filterIndexRepository.saveIndex(
                    productId,
                    product.getCategoryId(),
                    value.getAttributeId(),
                    valueId
            );
        }
    }


    @Transactional
    public void removeAttribute(Long productId, Long attributeValueId) {

        productAttributeRepository.removeAttributeFromProduct(productId, attributeValueId);

        // проще всего — пересобрать индекс
        rebuildIndex(productId);
    }


    @Transactional
    public void replaceAttributes(Long productId, List<Long> newValueIds) {

        ProductModel product = getProductOrThrow(productId);

        // удалить старые связи
        productAttributeRepository.removeAllByProductId(productId);

        // удалить индекс
        filterIndexRepository.deleteByProductId(productId);

        // добавить новые
        for (Long valueId : newValueIds) {

            AttributeValueModel value = getValueOrThrow(valueId);

            productAttributeRepository.addAttributeToProduct(productId, valueId);

            filterIndexRepository.saveIndex(
                    productId,
                    product.getCategoryId(),
                    value.getAttributeId(),
                    valueId
            );
        }
    }

    @Transactional
    public void rebuildIndex(Long productId) {

        ProductModel product = getProductOrThrow(productId);

        List<Long> valueIds = productAttributeRepository
                .findAttributeValueIdsByProductId(productId);

        filterIndexRepository.deleteByProductId(productId);

        for (Long valueId : valueIds) {

            AttributeValueModel value = getValueOrThrow(valueId);

            filterIndexRepository.saveIndex(
                    productId,
                    product.getCategoryId(),
                    value.getAttributeId(),
                    valueId
            );
        }
    }

    // HELPERS
    private ProductModel getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found"));
    }

    private AttributeValueModel getValueOrThrow(Long id) {
        return attributeValueRepository.findById(id)
                .orElseThrow(() -> new ProductException("Attribute value not found"));
    }
}