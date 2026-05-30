package org.example.mebkuch.infrastructure.persistence.repositories.product;


import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ComponentException;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.exception.ProductStyleException;
import org.example.mebkuch.domain.models.filter.ComponentFilter;
import org.example.mebkuch.domain.models.filter.ProductFilter;
import org.example.mebkuch.domain.models.product.ProductExtraFieldsModel;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentEntity;
import org.example.mebkuch.infrastructure.persistence.entities.component.ProductComponentEntity;
import org.example.mebkuch.infrastructure.persistence.entities.component.ProductComponentId;
import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeValueEntity;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStyleEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.product.ProductModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.category.CategoryRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component.ComponentRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.AttributeValueRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.*;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
@RequiredArgsConstructor
@Primary
public class ProductRepositoryImpl implements IProductRepository {

    private final ProductRepositoryJpa productRepositoryJpa;
    private final ProductStyleRepositoryJpa productStyleRepositoryJpa;
    private final ComponentRepositoryJpa componentRepositoryJpa;
    private final AttributeValueRepositoryJpa attributeValueRepositoryJpa;

    private final CategoryRepositoryJpa categoryRepo;
    private final ProductTypeRepositoryJpa typeRepo;
    private final ProductStatusRepositoryJpa statusRepo;
    private final ProductStyleRepositoryJpa styleRepo;
    private final ProductSectionRepositoryJpa sectionRepo;

    @Override
    public Optional<ProductModel> findById(Long id) {
        return productRepositoryJpa.findById(id)
                .map(ProductModelEntityMapper::toModel);
    }

    @Override
    public Optional<ProductModel> save(ProductModel productModel) {
        ProductEntity entity = ProductModelEntityMapper.toEntityForCreate(productModel,
                categoryRepo, typeRepo, statusRepo, styleRepo, sectionRepo
        );

        ProductEntity saved = productRepositoryJpa.save(entity);
        return Optional.of(ProductModelEntityMapper.toModel(saved));
    }

    @Override
    public boolean deleteById(Long id) {
        if (!productRepositoryJpa.existsById(id)) {
            return false;
        }
        productRepositoryJpa.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public Optional<ProductModel> update(Long id, ProductExtraFieldsModel model) {
        ProductEntity productEntity = productRepositoryJpa.findById(id)
                .orElseThrow(() -> new ProductException("продукт не найден"));

        if (model.getDescription() != null && !model.getDescription().isBlank()) {
            productEntity.setDescription(model.getDescription());
        }

        if (model.getProductStyleId() != null) {
            ProductStyleEntity style = productStyleRepositoryJpa.findById(model.getProductStyleId())
                    .orElseThrow(() -> new ProductStyleException("такого стиля не существует"));
            productEntity.setProductStyleEntity(style);
        }

        // Обработка компонентов
        if (model.getComponents() != null) {
            for (Long componentId : model.getComponents()) {
                ComponentEntity componentEntity = componentRepositoryJpa.findById(componentId)
                        .orElseThrow(() -> new ComponentException("такого компонента не существует"));

                // Ищем существующий компонент в коллекции
                ProductComponentEntity existing = productEntity.getProductComponentEntities().stream()
                        .filter(pce -> pce.getComponent().equals(componentEntity))
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    existing.setQuantity(existing.getQuantity() + 1);
                } else {
                    // Создаём новый
                    ProductComponentEntity newPce = ProductComponentEntity.builder()
                            .id(new ProductComponentId(productEntity.getId(), componentEntity.getId()))
                            .product(productEntity)
                            .component(componentEntity)
                            .quantity(1L)
                            .build();
                    productEntity.getProductComponentEntities().add(newPce);
                }
            }
        }

        if (model.getAttributeValues() != null && !model.getAttributeValues().isEmpty()) {
            for (Long attrValueId : model.getAttributeValues()) {
                AttributeValueEntity attrValue = attributeValueRepositoryJpa.findById(attrValueId)
                        .orElseThrow(() -> new ProductException("такого атрибута с указанным значением не существует"));
                productEntity.getAttributeValues().add(attrValue);
            }
        }

        return Optional.of(ProductModelEntityMapper.toModel(productEntity));
    }

    @Override
    public boolean existsById(Long id) {
        return productRepositoryJpa.existsById(id);
    }

    @Override
    public Page<ProductModel> findAll(Pageable pageable) {
        return productRepositoryJpa.findAll(pageable)
                .map(ProductModelEntityMapper::toModel);
    }

    @Override
    public Page<ProductModel> getProductsByFilter(ProductFilter filter, Pageable pageable) {
        Specification<ProductEntity> spec = Specification.unrestricted();

        spec = getFullSpecification(filter, spec);

        return productRepositoryJpa.findAll(spec, pageable)
                .map(ProductModelEntityMapper::toModel);
    }

    @Override
    public Page<ProductModel> findByIds(Set<Long> ids, ProductFilter filter, Pageable pageable) {

        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }

        Specification<ProductEntity> spec = Specification
                .where((root, query, cb) -> root.get("id").in(ids));

        getFullSpecification(filter, spec);

        return productRepositoryJpa.findAll(spec, pageable)
                .map(ProductModelEntityMapper::toModel);
    }

    @Override
    public boolean existsBuStyleId(Long styleId) {
        return productRepositoryJpa.existsByProductStyleId(styleId);
    }

    @Override
    public boolean existsByStatusId(Long statusId) {
        return productRepositoryJpa.existsByProductStatusId(statusId);
    }

    @Override
    public boolean existsByTypeId(Long typeId) {
        return productRepositoryJpa.existsByProductTypeId(typeId);
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return productRepositoryJpa.existsByCategoryId(categoryId);
    }

    private Specification<ProductEntity> getFullSpecification(ProductFilter filter, Specification spec) {

        //  name
        if (filter.getName() != null && !filter.getName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")),
                            "%" + filter.getName().toLowerCase() + "%"));
        }

        //  price
        if (filter.getMinPrice() != null) {
            spec = spec.and((root, query, cb) -> {
                Expression<BigDecimal> discountedPrice =
                        cb.diff(
                                root.get("minPrice"),
                                cb.quot(
                                        cb.prod(root.get("minPrice"), root.get("discount")),
                                        cb.literal(new BigDecimal("100"))
                                )
                        );
                        return cb.greaterThanOrEqualTo(discountedPrice , filter.getMinPrice());
                    });

        }

        if (filter.getMaxPrice() != null) {
            spec = spec.and((root, query, cb) -> {
                        Expression<BigDecimal> discountedPrice =
                        cb.diff(
                                root.get("minPrice"),
                                cb.quot(
                                        cb.prod(root.get("minPrice"), root.get("discount")),
                                        cb.literal(new BigDecimal("100"))
                                )
                        );
                    return cb.lessThanOrEqualTo(discountedPrice, filter.getMaxPrice());
                }
            );

        }

        //  discount
        if (filter.getMinDiscount() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("discount"), filter.getMinDiscount()));
        }

        if (filter.getMaxDiscount() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("discount"), filter.getMaxDiscount()));
        }

        //  createdAt
        if (filter.getCreatedAt() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("createdAt"), filter.getCreatedAt()));
        }

        //  category
        if (filter.getCategoryId() != null && !filter.getCategoryId().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("сategoryEntity").get("id").in(filter.getCategoryId()));
        }

        //  productType
        if (filter.getProductTypeId() != null && !filter.getProductTypeId().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("productTypeEntity").get("id").in(filter.getProductTypeId()));
        }

        //  status
        if (filter.getProductStatusId() != null && !filter.getProductStatusId().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("productStatusEntity").get("id").in(filter.getProductStatusId()));
        }

        //  style
        if (filter.getProductStyleId() != null && !filter.getProductStyleId().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("productStyleEntity").get("id").in(filter.getProductStyleId()));
        }

        if (filter.getComponentFilter() != null) {

            spec = spec.and((root, query, cb) -> {

                query.distinct(true);
                ComponentFilter cf = filter.getComponentFilter();

                List<Predicate> mainPredicates = new ArrayList<>();

                // EXISTS — фильтр по name, material, country
                if (cf.getName() != null || cf.getMaterial() != null || cf.getCountry() != null) {

                    Subquery<Long> existsSubquery = query.subquery(Long.class);
                    Root<ProductComponentEntity> pc = existsSubquery.from(ProductComponentEntity.class);
                    Join<ProductComponentEntity, ComponentEntity> component = pc.join("component");

                    List<Predicate> existsPredicates = new ArrayList<>();

                    existsPredicates.add(cb.equal(pc.get("product"), root));

                    if (cf.getName() != null && !cf.getName().isBlank()) {
                        existsPredicates.add(cb.like(
                                cb.lower(component.get("name")),
                                "%" + cf.getName().toLowerCase() + "%"
                        ));
                    }

                    if (cf.getMaterial() != null && !cf.getMaterial().isBlank()) {
                        existsPredicates.add(cb.like(
                                cb.lower(component.get("material")),
                                "%" + cf.getMaterial().toLowerCase() + "%"
                        ));
                    }

                    if (cf.getCountry() != null && !cf.getCountry().isBlank()) {
                        existsPredicates.add(cb.like(
                                cb.lower(component.get("country")),
                                "%" + cf.getCountry().toLowerCase() + "%"
                        ));
                    }

                    existsSubquery.select(cb.literal(1L));
                    existsSubquery.where(existsPredicates.toArray(new Predicate[0]));

                    mainPredicates.add(cb.exists(existsSubquery));
                }

                // SUM — фильтр по стоимости (с теми же условиями)

                if (cf.getBeginCost() != null || cf.getEndCost() != null) {

                    Subquery<BigDecimal> sumSubquery = query.subquery(BigDecimal.class);
                    Root<ProductComponentEntity> pc = sumSubquery.from(ProductComponentEntity.class);
                    Join<ProductComponentEntity, ComponentEntity> component = pc.join("component");

                    Expression<BigDecimal> totalCost = cb.sum(
                            cb.prod(
                                    cb.coalesce(component.get("cost"), BigDecimal.ZERO),
                                    pc.get("quantity")
                            )
                    );

                    List<Predicate> sumPredicates = new ArrayList<>();
                    sumPredicates.add(cb.equal(pc.get("product"), root));

                    // те же фильтры
                    if (cf.getName() != null && !cf.getName().isBlank()) {
                        sumPredicates.add(cb.like(
                                cb.lower(component.get("name")),
                                "%" + cf.getName().toLowerCase() + "%"
                        ));
                    }

                    if (cf.getMaterial() != null && !cf.getMaterial().isBlank()) {
                        sumPredicates.add(cb.like(
                                cb.lower(component.get("material")),
                                "%" + cf.getMaterial().toLowerCase() + "%"
                        ));
                    }

                    if (cf.getCountry() != null && !cf.getCountry().isBlank()) {
                        sumPredicates.add(cb.like(
                                cb.lower(component.get("country")),
                                "%" + cf.getCountry().toLowerCase() + "%"
                        ));
                    }

                    sumSubquery.select(totalCost);
                    sumSubquery.where(sumPredicates.toArray(new Predicate[0]));

                    if (cf.getBeginCost() != null) {
                        mainPredicates.add(cb.greaterThanOrEqualTo(sumSubquery, cf.getBeginCost()));
                    }

                    if (cf.getEndCost() != null) {
                        mainPredicates.add(cb.lessThanOrEqualTo(sumSubquery, cf.getEndCost()));
                    }
                }

                return cb.and(mainPredicates.toArray(new Predicate[0]));
            });
        }
        return spec;
    }

}
