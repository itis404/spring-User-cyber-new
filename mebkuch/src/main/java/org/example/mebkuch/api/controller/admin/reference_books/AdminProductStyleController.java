package org.example.mebkuch.api.controller.admin.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.reference_books.ProductStyleDto;
import org.example.mebkuch.api.validation.ProductStyleValidatorDto;
import org.example.mebkuch.application.usecase.reference_books.style.ProductStyleCommandUseCase;
import org.example.mebkuch.application.usecase.reference_books.style.ProductStyleQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/admin/product-style")
@RequiredArgsConstructor
public class AdminProductStyleController {

    private final ProductStyleCommandUseCase commandUseCase;
    private final ProductStyleQueryUseCase queryUseCase;


    @PostMapping
    public ResponseEntity<ProductStyleDto> create(@RequestBody ProductStyleDto dto) {
        ProductStyleValidatorDto.validate(dto);

        ProductStyleDto productStyleDto = commandUseCase.create(dto);
        log.info(dto.toString());
        return ResponseEntity.ok().body(productStyleDto);
    }


    @PatchMapping("/{id}/name")
    public ResponseEntity<ProductStyleDto> updateName(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        ProductStyleValidatorDto.validateName(name);
        ProductStyleDto productStyleDto = commandUseCase.update(id, name);
        return ResponseEntity.ok().body(productStyleDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandUseCase.delete(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductStyleDto> getById(@PathVariable Long id) {

        ProductStyleDto productStyleDto = queryUseCase.getById(id);

        return ResponseEntity.ok().body(productStyleDto);
    }


    @GetMapping
    public Page<ProductStyleDto> getAll(@ParameterObject
                                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                        Pageable pageable) {
        return queryUseCase.getAll(pageable);
    }
}