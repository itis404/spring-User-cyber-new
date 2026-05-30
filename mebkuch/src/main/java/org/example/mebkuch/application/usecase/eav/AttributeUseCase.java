package org.example.mebkuch.application.usecase.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.eav.AttributeDto;
import org.example.mebkuch.domain.models.eav.AttributeModel;
import org.example.mebkuch.domain.service.eav.AttributeService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttributeUseCase {

    private final AttributeService attributeService;

    public AttributeDto create(AttributeDto dto) {
        return toDto(attributeService.create(toModel(dto)));
    }

    public void delete(Long id) {
        attributeService.delete(id);
    }

    public AttributeDto getById(Long id) {
        return toDto(attributeService.getById(id));
    }

    public List<AttributeDto> getAll() {
        return attributeService.getAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private AttributeDto toDto(AttributeModel model) {
        return AttributeDto.builder()
                .id(model.getId())
                .name(model.getName())
                .type(model.getType())
                .build();
    }

    private AttributeModel toModel(AttributeDto dto) {
        return AttributeModel.builder()
                .name(dto.getName())
                .type(dto.getType())
                .build();
    }
}