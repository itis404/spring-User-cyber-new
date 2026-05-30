package org.example.mebkuch.domain.service.eav;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.models.eav.AttributeModel;
import org.example.mebkuch.domain.models.eav.AttributeValueModel;
import org.example.mebkuch.domain.repository.eav.IAttributeRepository;
import org.example.mebkuch.domain.repository.eav.IAttributeValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeValueService {

    private final IAttributeValueRepository valueRepository;
    private final IAttributeRepository attributeRepository;

    public AttributeValueModel create(AttributeValueModel value) {

        AttributeModel attribute = getAttributeOrThrow(value.getAttributeId());

        validateValueByType(attribute, value);

        checkDuplicate(value);

        return valueRepository.save(value);
    }


    public boolean delete(Long id) {
        return valueRepository.deleteById(id);
    }

    public List<AttributeValueModel> getByAttribute(Long attributeId) {
        return valueRepository.findByAttributeId(attributeId);
    }

    private AttributeModel getAttributeOrThrow(Long attributeId) {
        return attributeRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalStateException("Attribute not found"));
    }


    private void validateValueByType(AttributeModel attribute, AttributeValueModel value) {

        int count = 0;

        if (value.getValueText() != null) count++;
        if (value.getValueNumber() != null) count++;
        if (value.getValueBoolean() != null) count++;

        if (count != 1) {
            log.error("Invalid value for attribute {}: expected exactly one non-null value, got {}", attribute.getName(), count);
            throw new ProductException("Exactly one value must be set");
        }

        switch (attribute.getType()) {

            case TEXT -> {
                if (value.getValueText() == null) {
                    log.error("Invalid TEXT value for attribute {}: expected non-null value", attribute.getName());
                    throw new ProductException("Expected TEXT value");
                }
            }

            case NUMBER -> {
                if (value.getValueNumber() == null) {
                    log.error("Invalid NUMBER value for attribute {}: expected non-null value", attribute.getName());
                    throw new ProductException("Expected NUMBER value");
                }
            }

            case BOOLEAN -> {
                if (value.getValueBoolean() == null) {
                    log.error("Invalid BOOLEAN value for attribute {}: expected non-null value", attribute.getName());
                    throw new ProductException("Expected BOOLEAN value");
                }
            }
        }
    }


    private void checkDuplicate(AttributeValueModel value) {

        Long attrId = value.getAttributeId();

        if (value.getValueText() != null &&
                valueRepository.existsByAttributeIdAndValueText(attrId, value.getValueText())) {
            throw new ProductException("Duplicate text value");
        }

        if (value.getValueNumber() != null &&
                valueRepository.existsByAttributeIdAndValueNumber(attrId, value.getValueNumber())) {
            throw new ProductException("Duplicate number value");
        }

        if (value.getValueBoolean() != null &&
                valueRepository.existsByAttributeIdAndValueBoolean(attrId, value.getValueBoolean())) {
            throw new ProductException("Duplicate boolean value");
        }
    }
}