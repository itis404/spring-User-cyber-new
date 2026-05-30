package org.example.mebkuch.domain.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.exception.ProductTypeException;
import org.example.mebkuch.domain.models.filter.ProductFilter;
import org.example.mebkuch.domain.models.product.ProductExtraFieldsModel;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.models.reference_book.ProductTypeModel;
import org.example.mebkuch.domain.repository.eav.IProductAttributeFilterRepository;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.example.mebkuch.domain.repository.reference_books.IProductTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final IProductRepository productRepository;
    private final IProductAttributeFilterRepository attributeFilterRepository;
    private final IProductTypeRepository productTypeRepository;


    public Page<ProductModel> getProducts(ProductFilter productFilter, Pageable pageable){

        int maxSize = 50;

        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted() || pageable.getSort().isEmpty()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );

        // если нет EAV фильтра — обычный поиск
        if (productFilter.getAttributes() == null
                || productFilter.getAttributes().isEmpty()
                || productFilter.getAttributes().stream().anyMatch(attributeFilter
                -> attributeFilter.getValueText()==null &&
                attributeFilter.getValueBoolean()==null && attributeFilter.getMinValue() == null && attributeFilter.getMaxValue() == null) ) {
            return productRepository.getProductsByFilter(productFilter, safePageable);
        }

        // получаем product_ids по EAV
        Set<Long> productIds = attributeFilterRepository
                .findProductIdsByAttributes(productFilter.getAttributes());

        // если ничего не найдено — сразу пустая страница
        if (productIds.isEmpty()) {
            log.info("id продуктов: + " + productIds.toString());
            return Page.empty(safePageable);
        }

        // применяем основной фильтр + IDs
        return productRepository.findByIds(productIds, productFilter, safePageable);
    }

    public Page<ProductModel> getProductWithMaxDiscount(Pageable pageable){
        int maxSize = 15;

        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                Sort.by("discount").descending()
        );

        return productRepository.findAll(safePageable);
    }

    public ProductModel addExtraFields(Long productId, ProductExtraFieldsModel productExtraFieldsModel){

        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("продукт с таким id е найден"));

        ProductTypeModel productTypeModel = productTypeRepository.findById(productModel.getProductTypeId())
                .orElseThrow(() -> new ProductTypeException("тип такого продукта не существует"));

        if( (productExtraFieldsModel.getComponents()==null || productExtraFieldsModel.getComponents().isEmpty() )
                && productTypeModel.getHasComponents()){
            throw new ProductException("для такого типа нельзя добавлять компоненты");
        }

        ProductModel productModel1 = productRepository.update(productId, productExtraFieldsModel)
                .orElseThrow(() -> new ProductException("проблемы с добавлением лполнительных полей"));

        log.info(productModel1.toString());

        return productModel1;
    }



    public ProductModel create(ProductModel product) {


        return productRepository.save(product).get();
    }


    public boolean delete(Long id) {

        if (!productRepository.existsById(id)) {
            return false;
        }

        return productRepository.deleteById(id);
    }

    public ProductModel getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found"));
    }


}
