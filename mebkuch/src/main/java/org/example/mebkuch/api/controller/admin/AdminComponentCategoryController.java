package org.example.mebkuch.api.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentCategoryDto;
import org.example.mebkuch.api.validation.ComponentCategoryValidatorDto;
import org.example.mebkuch.application.usecase.component.component_category.ComponentCategoryCommandUseCase;
import org.example.mebkuch.application.usecase.component.component_category.ComponentCategoryQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/component-category")
@RequiredArgsConstructor
public class AdminComponentCategoryController {
    private final ComponentCategoryCommandUseCase componentCategoryCommandUseCase;
    private final ComponentCategoryQueryUseCase componentCategoryQueryUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ComponentCategoryDto> getComponentCategory(@PathVariable Long id){
        ComponentCategoryDto componentCategoryDto = componentCategoryQueryUseCase.getById(id);

        return ResponseEntity.ok().body(componentCategoryDto);
    }

    @GetMapping
    public ResponseEntity<Page<ComponentCategoryDto>> getAllComponentCategory(@ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        Page<ComponentCategoryDto> componentCategoryDtos = componentCategoryQueryUseCase.getAll(pageable);

        return ResponseEntity.ok().body(componentCategoryDtos);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ComponentCategoryDto> updateName(@PathVariable Long id, @RequestParam String name){
        ComponentCategoryValidatorDto.validateName(name);
        ComponentCategoryDto componentCategoryDto = componentCategoryCommandUseCase.renameName(id, name);
        return ResponseEntity.ok().body(componentCategoryDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(Long id){
        componentCategoryCommandUseCase.deleteComponentCategory(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<ComponentCategoryDto> createComponentCategory(@RequestBody ComponentCategoryDto componentCategoryDto){
        ComponentCategoryValidatorDto.validate(componentCategoryDto);
        ComponentCategoryDto componentCategoryDtoCreated = componentCategoryCommandUseCase.createComponentCategory(componentCategoryDto);

        return ResponseEntity.ok().body(componentCategoryDtoCreated);
    }
}
