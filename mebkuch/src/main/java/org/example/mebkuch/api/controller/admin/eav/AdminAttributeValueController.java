package org.example.mebkuch.api.controller.admin.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.eav.AttributeValueDto;

import org.example.mebkuch.api.validation.AttributeValueValidatorDto;
import org.example.mebkuch.application.usecase.eav.AttributeValueUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/attribute-values")
@RequiredArgsConstructor
public class AdminAttributeValueController {

    private final AttributeValueUseCase useCase;

    @PostMapping
    public AttributeValueDto create(@RequestBody AttributeValueDto dto) {
        AttributeValueValidatorDto.validate(dto);
        return useCase.create(dto);
    }


    @GetMapping("/by-attribute/{attributeId}")
    public List<AttributeValueDto> getByAttribute(@PathVariable Long attributeId) {
        return useCase.getByAttribute(attributeId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        useCase.delete(id);
    }
}
