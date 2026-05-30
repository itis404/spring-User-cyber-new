package org.example.mebkuch.infrastructure.persistence.mapper.eav;

import org.example.mebkuch.domain.models.eav.AttributeModel;
import org.example.mebkuch.domain.models.eav.AttributeType;
import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeEntity;

public class AttributeModelEntityMapper {

    public static AttributeModel toModel(AttributeEntity entity) {
        if (entity == null) return null;

        return AttributeModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(AttributeType.valueOf(entity.getDataType()))
                .build();
    }

    public static AttributeEntity toEntity(AttributeModel model) {
        if (model == null) return null;

        return AttributeEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .dataType(model.getType().name())
                .build();
    }
}
