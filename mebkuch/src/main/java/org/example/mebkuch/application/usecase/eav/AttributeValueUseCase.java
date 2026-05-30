package org.example.mebkuch.application.usecase.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.eav.AttributeValueDto;
import org.example.mebkuch.domain.models.eav.AttributeValueModel;
import org.example.mebkuch.domain.service.eav.AttributeValueService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttributeValueUseCase {

    private final AttributeValueService attributeValueService;

    public AttributeValueDto create(AttributeValueDto dto) {
        return toDto(attributeValueService.create(toModel(dto)));
    }


    public void delete(Long id) {
        attributeValueService.delete(id);
    }

    public List<AttributeValueDto> getByAttribute(Long attributeId) {
        return attributeValueService.getByAttribute(attributeId)
                .stream()
                .map(this::toDto)
                .toList();
    }


    private AttributeValueDto toDto(AttributeValueModel model) {
        return AttributeValueDto.builder()
                .id(model.getId())
                .attributeId(model.getAttributeId())
                .valueText(model.getValueText())
                .valueNumber(model.getValueNumber())
                .valueBoolean(model.getValueBoolean())
                .build();
    }

    private AttributeValueModel toModel(AttributeValueDto dto) {
        return AttributeValueModel.builder()
                .id(dto.getId())
                .attributeId(dto.getAttributeId())
                .valueText(dto.getValueText())
                .valueNumber(dto.getValueNumber())
                .valueBoolean(dto.getValueBoolean())
                .build();
    }
}