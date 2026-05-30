package org.example.mebkuch.api.mapper;

import org.example.mebkuch.api.dto.component.ComponentCategoryDto;
import org.example.mebkuch.domain.models.component.ComponentCategoryModel;

public class ComponentCategoryDtoModelMapper {

    public static ComponentCategoryDto toDto(ComponentCategoryModel componentCategoryModel){
        return ComponentCategoryDto.builder()
                .id(componentCategoryModel.getId())
                .name(componentCategoryModel.getName())
                .build();
    }

    public static ComponentCategoryModel toModel(ComponentCategoryDto componentCategoryDto){
        return ComponentCategoryModel.builder()
                .name(componentCategoryDto.getName())
                .build();
    }

}
