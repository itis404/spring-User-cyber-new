package org.example.mebkuch.application.usecase.component.component;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentDto;
import org.example.mebkuch.api.mapper.ComponentDtoModelMapper;
import org.example.mebkuch.domain.models.component.ComponentModel;
import org.example.mebkuch.domain.service.component.ComponentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComponentCommandUseCase {

    private final ComponentService componentService;

    public ComponentDto createComponent(ComponentDto componentDto){
        ComponentModel componentModel = componentService.save(ComponentDtoModelMapper.toModel(componentDto));

        return ComponentDtoModelMapper.toDto(componentModel);
    }

    public ComponentDto updateComponent(Long id, ComponentDto componentDto){
        componentDto.setId(id);
        ComponentModel componentModel = componentService.updateComponentAllFields(ComponentDtoModelMapper.toModel(componentDto));

        return ComponentDtoModelMapper.toDto(componentModel);
    }

    public void deleteById(Long id){
        componentService.deleteById(id);
    }
}
