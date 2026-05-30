package org.example.mebkuch.infrastructure.persistence.repositories.eav;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.models.filter.AttributeFilter;
import org.example.mebkuch.domain.repository.eav.IProductAttributeFilterRepository;
import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductFilterIndexEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ProductAttributeFilterRepositoryImpl implements IProductAttributeFilterRepository {

    private final EntityManager entityManager;

    @Override
    public Set<Long> findProductIdsByAttributes(List<AttributeFilter> filters) {

        if (filters == null || filters.isEmpty()) {
            return Set.of();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<ProductFilterIndexEntity> root = query.from(ProductFilterIndexEntity.class);
        var avJoin = root.join("attributeValue");

        List<Predicate> andPredicates = new ArrayList<>();

        for (AttributeFilter filter : filters) {

            List<Predicate> orPredicates = new ArrayList<>();

            // TEXT
            if (filter.getValueText() != null) {
                orPredicates.add(cb.and(
                        cb.equal(root.get("attributeId"), filter.getAttributeId()),
                        cb.equal(avJoin.get("valueText"), filter.getValueText())
                ));
            }

            // BOOLEAN
            if (filter.getValueBoolean() != null) {
                orPredicates.add(cb.and(
                        cb.equal(root.get("attributeId"), filter.getAttributeId()),
                        cb.equal(avJoin.get("valueBoolean"), filter.getValueBoolean())
                ));
            }

            // RANGE
            if (filter.getMinValue() != null || filter.getMaxValue() != null) {

                List<Predicate> range = new ArrayList<>();
                range.add(cb.equal(root.get("attributeId"), filter.getAttributeId()));

                if (filter.getMinValue() != null) {
                    range.add(cb.greaterThanOrEqualTo(
                            avJoin.get("valueNumber"),
                            filter.getMinValue()
                    ));
                }

                if (filter.getMaxValue() != null) {
                    range.add(cb.lessThanOrEqualTo(
                            avJoin.get("valueNumber"),
                            filter.getMaxValue()
                    ));
                }

                orPredicates.add(cb.and(range.toArray(new Predicate[0])));
            }

            if (!orPredicates.isEmpty()) {
                andPredicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
            }
        }

        query.select(root.get("productId"))
                .where(cb.and(andPredicates.toArray(new Predicate[0])))
                .distinct(true);

        return new HashSet<>(entityManager.createQuery(query).getResultList());
    }
}