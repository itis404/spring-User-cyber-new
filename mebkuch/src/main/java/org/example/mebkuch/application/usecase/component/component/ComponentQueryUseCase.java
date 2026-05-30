package org.example.mebkuch.application.usecase.component.component;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentDto;
import org.example.mebkuch.api.mapper.ComponentDtoModelMapper;
import org.example.mebkuch.domain.models.component.ComponentModel;
import org.example.mebkuch.domain.models.filter.ComponentFilter;
import org.example.mebkuch.domain.service.component.ComponentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentQueryUseCase {

    private final ComponentService componentService;

    public ComponentDto getComponent(Long id){
        ComponentModel componentModel = componentService.findById(id);

        return ComponentDtoModelMapper.toDto(componentModel);
    }

    public List<ComponentDto> getFixedBatch(Long cursor, int size){

        size = Math.min(size, 60);

        List<ComponentModel> componentModelList = componentService.getFixedBatch(cursor, size);

        return componentModelList.stream().map(ComponentDtoModelMapper::toDto).toList();
    }

    public ComponentDto findByName(String name){
        ComponentModel componentModel = componentService.findByName(name);

        return ComponentDtoModelMapper.toDto(componentModel);
    }

    public Page<ComponentDto> getByFilter(ComponentFilter componentFilter, Pageable pageable){

        return componentService.getComponentModelsWithFilter(componentFilter, pageable)
                .map(ComponentDtoModelMapper::toDto);
    }
}
