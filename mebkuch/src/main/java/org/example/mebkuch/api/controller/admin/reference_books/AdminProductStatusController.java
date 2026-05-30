package org.example.mebkuch.api.controller.admin.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductStatusDto;
import org.example.mebkuch.api.validation.ProductStatusValidatorDto;
import org.example.mebkuch.application.usecase.reference_books.status.ProductStatusCommandUseCase;
import org.example.mebkuch.application.usecase.reference_books.status.ProductStatusQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product-status")
@RequiredArgsConstructor
public class AdminProductStatusController {

    private final ProductStatusCommandUseCase commandUseCase;
    private final ProductStatusQueryUseCase queryUseCase;

    @PostMapping
    public ResponseEntity<ProductStatusDto> create(@RequestBody ProductStatusDto dto) {
        ProductStatusValidatorDto.validate(dto);

        ProductStatusDto productStatusDto = commandUseCase.create(dto);

        return ResponseEntity.ok().body(productStatusDto);
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<ProductStatusDto> updateName(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        ProductStatusValidatorDto.validateName(name);
        ProductStatusDto productStatusDto = commandUseCase.update(id, name);
        return ResponseEntity.ok().body(productStatusDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandUseCase.delete(id);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductStatusDto> getById(@PathVariable Long id) {

        ProductStatusDto productStatusDto = queryUseCase.getById(id);

        return ResponseEntity.ok().body(productStatusDto);
    }

    @GetMapping
    public Page<ProductStatusDto> getAll(@ParameterObject
                                         @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                         Pageable pageable) {
        return queryUseCase.getAll(pageable);
    }
}