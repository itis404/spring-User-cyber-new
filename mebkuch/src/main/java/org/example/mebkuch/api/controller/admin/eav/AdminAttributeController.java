package org.example.mebkuch.api.controller.admin.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.eav.AttributeDto;
import org.example.mebkuch.api.validation.AttributeValidatorDto;
import org.example.mebkuch.application.usecase.eav.AttributeUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/attributes")
@RequiredArgsConstructor
public class AdminAttributeController {

    private final AttributeUseCase useCase;

    @PostMapping
    public AttributeDto create(@RequestBody AttributeDto dto) {
        AttributeValidatorDto.validate(dto);
        return useCase.create(dto);
    }


    @GetMapping("/{id}")
    public AttributeDto get(@PathVariable Long id) {
        return useCase.getById(id);
    }

    @GetMapping
    public List<AttributeDto> getAll() {
        return useCase.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        useCase.delete(id);
    }
}