package org.example.mebkuch.infrastructure.persistence.mapper.component;

import org.example.mebkuch.domain.models.component.ComponentCategoryModel;
import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentCategoryEntity;

public class ComponentCategoryModelEntityMapper {

    public static ComponentCategoryEntity toEntity(ComponentCategoryModel componentCategoryModel){
        return ComponentCategoryEntity.builder()
                .name(componentCategoryModel.getName())
                .build();
    }

    public static ComponentCategoryModel toModel(ComponentCategoryEntity componentCategoryEntity){
        return ComponentCategoryModel.builder()
                .id(componentCategoryEntity.getId())
                .name(componentCategoryEntity.getName())
                .build();
    }
}
