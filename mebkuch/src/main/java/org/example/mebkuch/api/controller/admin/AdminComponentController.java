package org.example.mebkuch.api.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.component.ComponentDto;
import org.example.mebkuch.api.validation.ComponentValidatorDto;
import org.example.mebkuch.application.usecase.component.component.ComponentCommandUseCase;
import org.example.mebkuch.application.usecase.component.component.ComponentQueryUseCase;
import org.example.mebkuch.domain.models.filter.ComponentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/component")
@RequiredArgsConstructor
public class AdminComponentController {

    private final ComponentQueryUseCase componentQueryUseCase;
    private final ComponentCommandUseCase componentCommandUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ComponentDto> getComponentDto(@PathVariable Long id){
        ComponentDto componentDto = componentQueryUseCase.getComponent(id);

        return ResponseEntity.ok().body(componentDto);
    }

    @GetMapping("/cursor")
    public ResponseEntity<List<ComponentDto>> getFixedBatch(@RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "20") int size){
        List<ComponentDto> componentDtoList = componentQueryUseCase.getFixedBatch(cursor, size);

        return ResponseEntity.ok().body(componentDtoList);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ComponentDto> updateComponentDto(@PathVariable Long id, @RequestBody ComponentDto componentDto){

        ComponentValidatorDto.validate(componentDto);

        ComponentDto componentDtoUpdated = componentCommandUseCase.updateComponent(id, componentDto);

        return ResponseEntity.ok().body(componentDtoUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable Long id){
        componentCommandUseCase.deleteById(id);

        return ResponseEntity.ok().build();
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

    @PostMapping
    public ResponseEntity<ComponentDto> createComponentDto(@RequestBody ComponentDto componentDto){

        ComponentValidatorDto.validate(componentDto);

        ComponentDto componentDtoCreated = componentCommandUseCase.createComponent(componentDto);

        return ResponseEntity.ok().body(componentDtoCreated);
    }

}
