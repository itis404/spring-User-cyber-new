package org.example.mebkuch.infrastructure.persistence.repositories.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.models.eav.AttributeValueModel;
import org.example.mebkuch.domain.repository.eav.IAttributeValueRepository;
import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeValueEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.eav.AttributeValueModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.AttributeRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.AttributeValueRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class AttributeValueRepositoryImpl implements IAttributeValueRepository {

    private final AttributeValueRepositoryJpa attributeValueRepositoryJpa;
    private final AttributeRepositoryJpa attributeRepositoryJpa;

    @Override
    public Optional<AttributeValueModel> findById(Long id) {
        return attributeValueRepositoryJpa.findById(id)
                .map(AttributeValueModelEntityMapper::toModel);
    }

    @Override
    public List<AttributeValueModel> findByAttributeId(Long attributeId) {
        return attributeValueRepositoryJpa.findByAttributeId(attributeId)
                .stream()
                .map(AttributeValueModelEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<AttributeValueModel> findByAttributeIdAndTextValue(Long attributeId, String value) {
        return attributeValueRepositoryJpa
                .findByAttributeIdAndValueText(attributeId, value)
                .stream()
                .map(AttributeValueModelEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<AttributeValueModel> findByAttributeIdAndNumberRange(Long attributeId, Double min, Double max) {
        return attributeValueRepositoryJpa
                .findByAttributeIdAndValueNumberBetween(
                        attributeId,
                        BigDecimal.valueOf(min),
                        BigDecimal.valueOf(max)
                )
                .stream()
                .map(AttributeValueModelEntityMapper::toModel)
                .toList();
    }

    @Override
    public List<AttributeValueModel> findByAttributeIdAndBooleanValue(Long attributeId, Boolean value) {
        return attributeValueRepositoryJpa
                .findByAttributeIdAndValueBoolean(attributeId, value)
                .stream()
                .map(AttributeValueModelEntityMapper::toModel)
                .toList();
    }

    @Override
    public AttributeValueModel save(AttributeValueModel value) {
        value.validate();

        AttributeValueEntity entity = AttributeValueModelEntityMapper.toEntity(value, attributeRepositoryJpa);

        return AttributeValueModelEntityMapper.toModel(
                attributeValueRepositoryJpa.save(entity)
        );
    }

    @Override
    public boolean deleteById(Long id) {
        if (!attributeValueRepositoryJpa.existsById(id)) return false;
        attributeValueRepositoryJpa.deleteById(id);
        return true;
    }

    @Override
    public void deleteByAttributeId(Long attributeId) {
        attributeValueRepositoryJpa.deleteByAttributeId(attributeId);
    }


    @Override
    public boolean existsByAttributeIdAndValueText(Long attributeId, String value) {
        return attributeValueRepositoryJpa
                .existsByAttributeIdAndValueText(attributeId, value);
    }

    @Override
    public boolean existsByAttributeIdAndValueNumber(Long attributeId, BigDecimal value) {
        return attributeValueRepositoryJpa
                .existsByAttributeIdAndValueNumber(attributeId, value);
    }

    @Override
    public boolean existsByAttributeIdAndValueBoolean(Long attributeId, Boolean value) {
        return attributeValueRepositoryJpa
                .existsByAttributeIdAndValueBoolean(attributeId, value);
    }
}
