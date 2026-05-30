package org.example.mebkuch.api.controller.admin.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductTypeDto;
import org.example.mebkuch.api.validation.ProductTypeValidatorDto;
import org.example.mebkuch.application.usecase.reference_books.type.ProductTypeCommandUseCase;
import org.example.mebkuch.application.usecase.reference_books.type.ProductTypeQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/product-type")
@RequiredArgsConstructor
public class AdminProductTypeController {

    private final ProductTypeCommandUseCase commandUseCase;
    private final ProductTypeQueryUseCase queryUseCase;


    @PostMapping
    public ResponseEntity<ProductTypeDto> create(@RequestBody ProductTypeDto dto) {
        ProductTypeValidatorDto.validate(dto);
        ProductTypeDto productTypeDto = commandUseCase.create(dto);

        return ResponseEntity.ok().body(productTypeDto);
    }


    @PatchMapping("/{id}/name")
    public ResponseEntity<ProductTypeDto> updateName(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        ProductTypeValidatorDto.validateName(name);
        ProductTypeDto productTypeDto = commandUseCase.update(id, name);

        return ResponseEntity.ok().body(productTypeDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        commandUseCase.delete(id);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductTypeDto> getById(@PathVariable Long id) {

        ProductTypeDto productTypeDto = queryUseCase.getById(id);

        return ResponseEntity.ok().body(productTypeDto);
    }


    @GetMapping
    public Page<ProductTypeDto> getAll(@ParameterObject
                                       @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                       Pageable pageable) {

        return queryUseCase.getAll(pageable);
    }
}