package org.example.mebkuch.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.eav.AttributeDto;
import org.example.mebkuch.application.usecase.eav.AttributeUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeUseCase useCase;

    @GetMapping
    public List<AttributeDto> getAll() {
        return useCase.getAll();
    }

}
