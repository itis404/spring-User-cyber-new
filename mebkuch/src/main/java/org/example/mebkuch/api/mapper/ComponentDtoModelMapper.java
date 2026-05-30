package org.example.mebkuch.api.mapper;


import org.example.mebkuch.api.dto.component.ComponentDto;
import org.example.mebkuch.domain.models.component.ComponentModel;

public class ComponentDtoModelMapper {

    public static ComponentModel toModel(ComponentDto componentDto){
        return ComponentModel.builder()
                .id(componentDto.getId())
                .name(componentDto.getName())
                .categoryId(componentDto.getCategoryId())
                .material(componentDto.getMaterial())
                .country(componentDto.getCountry())
                .cost(componentDto.getCost())
                .build();
    }

    public static ComponentDto toDto(ComponentModel componentModel){
        return ComponentDto.builder()
                .id(componentModel.getId())
                .name(componentModel.getName())
                .categoryId(componentModel.getCategoryId())
                .material(componentModel.getMaterial())
                .country(componentModel.getCountry())
                .cost(componentModel.getCost())
                .build();
    }

}
