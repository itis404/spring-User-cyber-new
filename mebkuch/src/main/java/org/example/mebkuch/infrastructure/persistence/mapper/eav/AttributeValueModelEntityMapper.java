package org.example.mebkuch.infrastructure.persistence.mapper.eav;

import org.example.mebkuch.domain.models.eav.AttributeValueModel;
import org.example.mebkuch.domain.repository.eav.IAttributeValueRepository;
import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeEntity;
import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeValueEntity;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.AttributeRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.AttributeValueRepositoryJpa;

public class AttributeValueModelEntityMapper {

    public static AttributeValueModel toModel(AttributeValueEntity entity) {
        if (entity == null) return null;

        return AttributeValueModel.builder()
                .id(entity.getId())
                .attributeId(entity.getAttribute().getId())
                .valueText(entity.getValueText())
                .valueNumber(entity.getValueNumber())
                .valueBoolean(entity.getValueBoolean())
                .build();
    }

    public static AttributeValueEntity toEntity(AttributeValueModel model, AttributeRepositoryJpa attributeRepositoryJpa) {
        if (model == null) return null;

        AttributeEntity attribute = attributeRepositoryJpa.getById(model.getAttributeId());

        return AttributeValueEntity.builder()
                .attribute(attribute)
                .valueText(model.getValueText())
                .valueNumber(model.getValueNumber())
                .valueBoolean(model.getValueBoolean())
                .build();
    }
}
