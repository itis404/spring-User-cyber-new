package org.example.mebkuch.infrastructure.persistence.mapper.component;

import org.example.mebkuch.domain.exception.ComponentException;
import org.example.mebkuch.domain.models.component.ComponentModel;
import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentCategoryEntity;
import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentEntity;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component.ComponentCategoryJpa;

public class ComponentModelEntityMapper {

    public static ComponentModel toModel(ComponentEntity componentEntity){
        return ComponentModel.builder()
                .id(componentEntity.getId())
                .name(componentEntity.getName())
                .categoryId(componentEntity.getCategory().getId())
                .material(componentEntity.getMaterial())
                .country(componentEntity.getCountry())
                .cost(componentEntity.getCost())
                .build();
    }


    public static ComponentEntity toEntity(ComponentModel componentModel, ComponentCategoryJpa componentCategoryJpa){

        ComponentCategoryEntity componentCategory = componentCategoryJpa
                .findById(componentModel.getCategoryId())
                .orElseThrow(() -> new ComponentException("категории компонента по данному id не существует"));


        return ComponentEntity.builder()
                .name(componentModel.getName())
                .category(componentCategory)
                .material(componentModel.getMaterial())
                .country(componentModel.getCountry())
                .cost(componentModel.getCost())
                .build();
    }

}
