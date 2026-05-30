package org.example.mebkuch.application.usecase.component.component_category;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentCategoryDto;
import org.example.mebkuch.api.mapper.ComponentCategoryDtoModelMapper;
import org.example.mebkuch.domain.models.component.ComponentCategoryModel;
import org.example.mebkuch.domain.service.component.ComponentCategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComponentCategoryCommandUseCase {

    private final ComponentCategoryService componentCategoryService;

    public ComponentCategoryDto createComponentCategory(ComponentCategoryDto componentCategoryDto){
        ComponentCategoryModel componentCategoryModel = componentCategoryService.createComponentCategoryModel(ComponentCategoryDtoModelMapper
                .toModel(componentCategoryDto));

        return ComponentCategoryDtoModelMapper.toDto(componentCategoryModel);
    }

    public ComponentCategoryDto renameName(Long id, String name){
        return ComponentCategoryDtoModelMapper.toDto(componentCategoryService
                .updateNameById(id, name));
    }

    public void deleteComponentCategory(Long id){
        componentCategoryService.deleteById(id);
    }


}