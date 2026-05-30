package org.example.mebkuch.application.usecase.component.component_category;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentCategoryDto;
import org.example.mebkuch.api.mapper.ComponentCategoryDtoModelMapper;
import org.example.mebkuch.domain.service.component.ComponentCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentCategoryQueryUseCase {

    private final ComponentCategoryService componentCategoryService;

    public Page<ComponentCategoryDto> getAll(Pageable pageable){
        return componentCategoryService.findAll(pageable)
                .map(ComponentCategoryDtoModelMapper::toDto);
    }

    public ComponentCategoryDto getById(Long id){
        return ComponentCategoryDtoModelMapper.toDto(componentCategoryService.findById(id));
    }

    public ComponentCategoryDto getByName(String name){
        return ComponentCategoryDtoModelMapper.toDto(componentCategoryService.findByName(name));
    }
}
