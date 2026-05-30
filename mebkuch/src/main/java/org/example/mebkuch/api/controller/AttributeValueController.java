package org.example.mebkuch.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.eav.AttributeValueDto;
import org.example.mebkuch.application.usecase.eav.AttributeValueUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attribute-values")
@RequiredArgsConstructor
public class AttributeValueController {

    private final AttributeValueUseCase useCase;

    @GetMapping("/by-attribute/{attributeId}")
    public List<AttributeValueDto> getByAttribute(@PathVariable Long attributeId) {
        return useCase.getByAttribute(attributeId);
    }
}
