package org.example.mebkuch.api.controller.category;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentCategoryDto;
import org.example.mebkuch.application.usecase.component.component_category.ComponentCategoryQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/component-category")
@RequiredArgsConstructor
public class ComponentCategoryController {

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



}