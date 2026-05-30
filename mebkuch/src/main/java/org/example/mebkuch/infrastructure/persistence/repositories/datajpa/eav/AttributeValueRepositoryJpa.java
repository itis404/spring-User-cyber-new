package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav;

import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AttributeValueRepositoryJpa extends JpaRepository<AttributeValueEntity, Long> {

    List<AttributeValueEntity> findByAttributeId(Long attributeId);

    List<AttributeValueEntity> findByAttributeIdAndValueText(Long attributeId, String value);

    List<AttributeValueEntity> findByAttributeIdAndValueNumberBetween(Long attributeId, BigDecimal min, BigDecimal max);

    List<AttributeValueEntity> findByAttributeIdAndValueBoolean(Long attributeId, Boolean value);

    boolean existsByAttributeIdAndValueText(Long attributeId, String value);

    boolean existsByAttributeIdAndValueNumber(Long attributeId, BigDecimal value);

    boolean existsByAttributeIdAndValueBoolean(Long attributeId, Boolean value);

    void deleteByAttributeId(Long attributeId);
}
