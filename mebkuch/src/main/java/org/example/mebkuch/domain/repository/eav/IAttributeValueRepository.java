package org.example.mebkuch.domain.repository.eav;

import org.example.mebkuch.domain.models.eav.AttributeValueModel;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAttributeValueRepository {
    Optional<AttributeValueModel> findById(Long id);

    List<AttributeValueModel> findByAttributeId(Long attributeId);

    List<AttributeValueModel> findByAttributeIdAndTextValue(Long attributeId, String value);

    List<AttributeValueModel> findByAttributeIdAndNumberRange(
            Long attributeId,
            Double min,
            Double max
    );

    List<AttributeValueModel> findByAttributeIdAndBooleanValue(
            Long attributeId,
            Boolean value
    );

    AttributeValueModel save(AttributeValueModel value);

    boolean deleteById(Long id);

    void deleteByAttributeId(Long attributeId);


    boolean existsByAttributeIdAndValueText(Long attributeId, String value);

    boolean existsByAttributeIdAndValueNumber(Long attributeId, BigDecimal value);

    boolean existsByAttributeIdAndValueBoolean(Long attributeId, Boolean value);
}