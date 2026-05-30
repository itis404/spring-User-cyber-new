package org.example.mebkuch.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentDto;
import org.example.mebkuch.application.usecase.component.component.ComponentQueryUseCase;
import org.example.mebkuch.domain.models.filter.ComponentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/component")
@RequiredArgsConstructor
public class ComponentController {

    private final ComponentQueryUseCase componentQueryUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ComponentDto> getComponentDto(@PathVariable Long id){
        ComponentDto componentDto = componentQueryUseCase.getComponent(id);

        return ResponseEntity.ok().body(componentDto);
    }

    @GetMapping
    public ResponseEntity<Page<ComponentDto>> getComponents(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) String material,
                                                            @RequestParam(required = false) String country,
                                                            @RequestParam(required = false, name = "begin-cost") BigDecimal beginCost,
                                                            @RequestParam(required = false, name = "end-cost") BigDecimal endCost,
                                                            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                            Pageable pageable){

        ComponentFilter componentFilter = ComponentFilter.builder()
                .name(name)
                .material(material)
                .country(country)
                .beginCost(beginCost)
                .endCost(endCost)
                .build();

        Page<ComponentDto> componentDtos = componentQueryUseCase.getByFilter(componentFilter, pageable);

        return ResponseEntity.ok().body(componentDtos);
    }
}
